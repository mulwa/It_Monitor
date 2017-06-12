package cj_server.com.itmonitor.Adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cj_server.com.itmonitor.Pojo.NewClass;
import cj_server.com.itmonitor.R;
import cj_server.com.itmonitor.databinding.classItemBinding;

/**
 * Created by cj-sever on 4/11/17.
 */

public class classAdapter extends RecyclerView.Adapter<classAdapter.BindingHolder> {
    private ArrayList<NewClass> classlist;

    public classAdapter(ArrayList<NewClass> classlist) {
        this.classlist = classlist;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_custom_layout,parent,false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        classItemBinding binding = holder.binding;
        binding.setItem(classlist.get(position));

    }

    @Override
    public int getItemCount() {
        return classlist.size();
    }

    public static  class BindingHolder  extends RecyclerView.ViewHolder{
        private classItemBinding binding;

        public BindingHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);

        }
    }
}
