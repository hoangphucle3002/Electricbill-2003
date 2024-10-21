package com.example.electric.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electric.R;
import com.example.electric.model.Customer;
import com.example.electric.model.DatabaseManage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private final Context context;
    private final DatabaseManage dbHelper;

    // Constructor của adapter
    public CustomerAdapter(List<Customer> customerList, Context context, DatabaseManage dbHelper) {
        this.customerList = customerList;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    // Tạo ViewHolder cho RecyclerView
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    // Bind dữ liệu khách hàng vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        // Lấy các cài đặt hiển thị từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean showAddress = sharedPreferences.getBoolean("showAddress", true);
        boolean showElectricUsage = sharedPreferences.getBoolean("showElectricUsage", true);
        boolean showUserType = sharedPreferences.getBoolean("showUserType", true);
        boolean showPrice = sharedPreferences.getBoolean("showPrice", true);

        // Hiển thị thông tin khách hàng
        holder.tvCustomerName.setText("Name: " + customer.getName());
        holder.tvCustomerAddress.setText("Address: " + customer.getAddress());

        // Xác định loại người dùng (Private/Business)
        String userTypeText = customer.getUserTypeId() == 1 ? "Private" : "Business";
        holder.tvUserType.setText("User Type: " + userTypeText);

        // Định dạng và hiển thị số điện đã sử dụng
        DecimalFormat dfUsage = new DecimalFormat("#,###");
        String formattedElectricUsage = dfUsage.format(customer.getElectricUsage());
        holder.tvElectricUsage.setText("Electric Usage: " + formattedElectricUsage + " kWh");

        // Định dạng và hiển thị tháng thanh toán (MM/YYYY)
        int yyyymm = customer.getYyyymm();
        int year = yyyymm / 100;
        int month = yyyymm % 100;
        String formattedBillingMonthYear = String.format("%02d/%d", month, year);
        holder.tvBillingMonthYear.setText("Billing Month: " + formattedBillingMonthYear);

        // Tính và hiển thị tổng số tiền
        double finalPrice = calculateFinalPrice(customer);
        DecimalFormat dfPrice = new DecimalFormat("#,###");
        String formattedPrice = dfPrice.format(finalPrice);
        holder.tvFinalPrice.setText("Price: " + formattedPrice + " VND");

        // Xử lý sự kiện khi nhấn nút "Edit"
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerEdit.class);
            intent.putExtra("customerId", customer.getId());
            intent.putExtra("customerName", customer.getName());
            intent.putExtra("customerAddress", customer.getAddress());
            intent.putExtra("electricUsage", customer.getElectricUsage());
            intent.putExtra("yyyymm", customer.getYyyymm());
            intent.putExtra("userTypeId", customer.getUserTypeId());
            context.startActivity(intent); // Mở Activity chỉnh sửa
        });

        // Xử lý sự kiện khi nhấn nút "Delete"
        holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(customer, position));

        // Hiển thị hoặc ẩn các trường thông tin dựa trên cấu hình
        holder.tvCustomerAddress.setVisibility(showAddress ? View.VISIBLE : View.GONE);
        holder.tvElectricUsage.setVisibility(showElectricUsage ? View.VISIBLE : View.GONE);
        holder.tvUserType.setVisibility(showUserType ? View.VISIBLE : View.GONE);
        holder.tvFinalPrice.setVisibility(showPrice ? View.VISIBLE : View.GONE);
    }

    // Số lượng khách hàng trong danh sách
    @Override
    public int getItemCount() {
        return customerList.size();
    }

    // Hiển thị hộp thoại xác nhận xóa khách hàng
    private void showDeleteConfirmationDialog(Customer customer, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this customer?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCustomer(customer, position))
                .setNegativeButton("No", null)
                .show();
    }

    // Xóa khách hàng khỏi cơ sở dữ liệu
    private void deleteCustomer(Customer customer, int position) {
        if (customer.getId() == 0) {
            Log.e("CustomerAdapter", "Cannot delete customer with ID: 0");
            return;
        }

        boolean isDeleted = dbHelper.deleteCustomer(customer.getId());

        if (isDeleted) {
            customerList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, customerList.size());
            Log.d("CustomerAdapter", "Deleted customer with ID: " + customer.getId());
        } else {
            Log.e("CustomerAdapter", "Error deleting customer with ID: " + customer.getId());
        }
    }

    // Tính giá trị cuối cùng cho khách hàng dựa trên đơn giá và số điện sử dụng
    private double calculateFinalPrice(Customer customer) {
        double unitPrice = dbHelper.getUnitPrice(customer.getUserTypeId());
        return customer.getElectricUsage() * unitPrice;
    }

    // Cập nhật danh sách khách hàng sau khi tìm kiếm hoặc thay đổi
    public void updateList(List<Customer> newCustomerList) {
        customerList = new ArrayList<>(newCustomerList);
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    // Lớp ViewHolder chứa các thành phần giao diện của mỗi mục trong RecyclerView
    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvUserType, tvCustomerAddress, tvElectricUsage, tvBillingMonthYear, tvFinalPrice;
        Button btnDelete, btnEdit;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            tvElectricUsage = itemView.findViewById(R.id.tvElectricUsage);
            tvBillingMonthYear = itemView.findViewById(R.id.tvBillingMonthYear);
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
