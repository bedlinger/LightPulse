package at.ac.tgm.hit.medt.bedlinger.lightpulse;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LightshowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightshow);

        ViewPager2 viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("Lightshow " + (position + 1))
        ).attach();
    }

    private void setupViewPager(ViewPager2 viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        Intent intent = getIntent();
        boolean hasIntensityControl = intent.getBooleanExtra("hasIntensityControl", true);

        // Add your fragments here
        adapter.addFragment(LightshowFragment.newInstance(1, hasIntensityControl));
        adapter.addFragment(LightshowFragment.newInstance(2, hasIntensityControl));
        adapter.addFragment(LightshowFragment.newInstance(3, hasIntensityControl));
        adapter.addFragment(LightshowFragment.newInstance(4, hasIntensityControl));

        viewPager.setAdapter(adapter);
    }
}