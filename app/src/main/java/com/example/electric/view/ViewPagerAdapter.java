package com.example.electric.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electric.R;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private int[] images; // Mảng chứa danh sách các ảnh

    // Constructor nhận danh sách ảnh từ bên ngoài truyền vào
    public ViewPagerAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng item trong ViewPager
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gán hình ảnh tương ứng cho từng vị trí trong ViewPager
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length; // Số lượng trang là số lượng hình ảnh
    }

    // ViewHolder để giữ các view của từng item
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // Biến ImageView để hiển thị hình ảnh

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView); // Khởi tạo ImageView từ layout
        }
    }
}
