package com.ezymd.restaurantapp.filters.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.font.CustomTypeFace

class FilterValuesAdapter(private val context: Context, private val filterIndex: Int) :
    RecyclerView.Adapter<FilterValuesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.filter_value_item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        /* final Filter tmpFilter = Preferences.filters.get(filterIndex);
        myViewHolder.value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selected = tmpFilter.getSelected();
                if(myViewHolder.value.isChecked()) {
                    selected.add(tmpFilter.getValues().get(position));
                    tmpFilter.setSelected(selected);
                } else {
                    selected.remove(tmpFilter.getValues().get(position));
                    tmpFilter.setSelected(selected);
                }
                Preferences.filters.put(filterIndex, tmpFilter);
            }
        });
        myViewHolder.value.setText(tmpFilter.getValues().get(position));
        if(tmpFilter.getSelected().contains(tmpFilter.getValues().get(position))) {
            myViewHolder.value.setChecked(true);
        }*/
        setSelectedViews(myViewHolder, true)
        myViewHolder.itemView.setOnClickListener {

        }
    }

    private fun setSelectedViews(myViewHolder: MyViewHolder, isSelected: Boolean) {
        if (isSelected) {
            myViewHolder.name.typeface = CustomTypeFace.bold
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.black))
            myViewHolder.checkBox.isChecked = true
        } else {
            myViewHolder.name.typeface = CustomTypeFace.book
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.gray_9e9e9e))
            myViewHolder.checkBox.isChecked = false
        }

    }

    override fun getItemCount(): Int {
        return 3
    }

    class MyViewHolder(var container: View) : RecyclerView.ViewHolder(
        container
    ) {
        var name: SnapTextView
        var checkBox: CheckBox

        init {
            checkBox = container.findViewById(R.id.checkbox)
            name = container.findViewById(R.id.name)
        }
    }
}