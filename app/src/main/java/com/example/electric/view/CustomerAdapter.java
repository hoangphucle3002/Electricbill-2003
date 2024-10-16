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
import com.example.electric.model.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.math.BigDecimal;


public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private Context context;
    private DatabaseHelper dbHelper;

    public CustomerAdapter(List<Customer> customerList, Context context, DatabaseHelper dbHelper) {
        this.customerList = customerList;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean showAddress = sharedPreferences.getBoolean("showAddress", true);
        boolean showElectricUsage = sharedPreferences.getBoolean("showElectricUsage", true);
        boolean showUserType = sharedPreferences.getBoolean("showUserType", true);
        boolean showPrice = sharedPreferences.getBoolean("showPrice", true);

        // Bind customer data to TextViews
        holder.tvCustomerName.setText("Name: " + customer.getName());
        holder.tvCustomerAddress.setText("Address: " + customer.getAddress());

        // Format Electric Usage without .0
        DecimalFormat dfUsage = new DecimalFormat("#,###");
        String formattedElectricUsage = dfUsage.format(customer.getElectricUsage());
        holder.tvElectricUsage.setText("Electric Usage: " + formattedElectricUsage + " kWh");

        // Format Billing Month as "MM/YYYY"
        int yyyymm = customer.getYyyymm();
        int year = yyyymm / 100;
        int month = yyyymm % 100;
        String formattedBillingMonthYear = String.format("%02d/%d", month, year);
        holder.tvBillingMonthYear.setText("Billing Month: " + formattedBillingMonthYear);

        // Format final price
        double finalPrice = calculateFinalPrice(customer);

        // Sử dụng DecimalFormat để định dạng số tiền
        DecimalFormat dfPrice = new DecimalFormat("#,###");
        String formattedPrice = dfPrice.format(finalPrice);
        holder.tvFinalPrice.setText("Price: " + formattedPrice + " VND");

        // Handle "Edit" button click
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerEditActivity.class);
            intent.putExtra("customerId", customer.getId());
            intent.putExtra("customerName", customer.getName());
            intent.putExtra("customerAddress", customer.getAddress());
            intent.putExtra("electricUsage", customer.getElectricUsage());
            intent.putExtra("yyyymm", customer.getYyyymm());
            intent.putExtra("userTypeId", customer.getUserTypeId());
            context.startActivity(intent); // Start edit activity
        });

        // Handle "Delete" button click
        holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(customer, position));

        if (showAddress) {
            holder.tvCustomerAddress.setVisibility(View.VISIBLE);
            holder.tvCustomerAddress.setText("Address: " + customer.getAddress());
        } else {
            holder.tvCustomerAddress.setVisibility(View.GONE);
        }

        if (showElectricUsage) {
            holder.tvElectricUsage.setVisibility(View.VISIBLE);
            holder.tvElectricUsage.setText("Electric Usage: " + formattedElectricUsage + " kWh");
        } else {
            holder.tvElectricUsage.setVisibility(View.GONE);
        }

        if (showPrice) {
            holder.tvFinalPrice.setVisibility(View.VISIBLE);
            holder.tvFinalPrice.setText("Price: " + formattedPrice + " VND");
        } else {
            holder.tvFinalPrice.setVisibility(View.GONE);
        }
    }




    @Override
    public int getItemCount() {
        return customerList.size();
    }

    private void showDeleteConfirmationDialog(Customer customer, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this customer?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCustomer(customer, position))
                .setNegativeButton("No", null)
                .show();
    }

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

    private double calculateFinalPrice(Customer customer) {
        double unitPrice = customer.getUserTypeId() == 1 ? 1000 : 2000;
        return customer.getElectricUsage() * unitPrice;
    }

    // Method to update customer list after search or changes
    public void updateList(List<Customer> newCustomerList) {
        customerList = new ArrayList<>(newCustomerList);
        notifyDataSetChanged(); // Notify RecyclerView to update data
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerAddress, tvElectricUsage, tvBillingMonthYear, tvFinalPrice;
        Button btnDelete, btnEdit;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvElectricUsage = itemView.findViewById(R.id.tvElectricUsage);
            tvBillingMonthYear = itemView.findViewById(R.id.tvBillingMonthYear);
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
