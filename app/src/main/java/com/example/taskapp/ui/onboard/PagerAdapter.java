package com.example.taskapp.ui.onboard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.taskapp.R;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private static final int NUM_ITEMS=3;

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

        container.addView(view);
        return view;
    }
    @Override
    public int getCount() {
        return NUM_ITEMS ;
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
