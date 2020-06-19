package com.example.taskapp.ui.onboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.taskapp.Prefs;
import com.example.taskapp.R;

import static com.example.taskapp.R.id.boardFragment;
import static com.example.taskapp.R.id.btn_skip;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    private NavController navController;

    public interface OnStartClickListener{
        void OnClick();
    }

    private OnStartClickListener onStartClickListener;

    public void setOnStartClickListener(OnStartClickListener onStartClickListener) {
        this.onStartClickListener = onStartClickListener;
    }

    private String[] titles = new String[]{"Аккуратный программист — быстрый программист",
            "Настоящий программист гораздо больше читает, чем пишет",
            "Прежде, чем начать сеанс парного программирования, уберите из комнаты все острые предметы"};
    private String[] descriptions = new String[]{"","",""};
    private int[] imageViews = new int[]{R.drawable.imag_one,R.drawable.imeg_two,R.drawable.imeg_three};

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.page_onboard,container,false);


        TextView textTitle = view.findViewById(R.id.textTitle);
        textTitle.setText(titles[position]);

        TextView textDescription = view.findViewById(R.id.textDescription);
        textDescription.setText(descriptions[position]);

        ImageView imageView  = view.findViewById(R.id.imageView);
        imageView.setImageResource(imageViews[position]);


        Button button_start = view.findViewById(R.id.btnStart);
        if (position == 2) button_start.setVisibility(View.VISIBLE);
        else button_start.setVisibility(View.GONE);



        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartClickListener.OnClick();
            }
        });
//        NavController navController = Navigation.setViewNavController();
//        Button button_skip = view.findViewById(R.id.btn_skip);
//        if (position == 2) button_skip.setVisibility(View.GONE);
//        else button_skip.setVisibility(View.VISIBLE);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
       // super.destroyItem(container, position, object);
    }
}
