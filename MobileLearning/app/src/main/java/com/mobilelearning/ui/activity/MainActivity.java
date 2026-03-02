package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mobilelearning.R;
import com.mobilelearning.utils.SpUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvTitle;
    private Button btnCourseList;
    private Button btnExamList;
    private Button btnProfile;
    private Button btnLogout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        tvTitle = findViewById(R.id.tv_title);
        btnCourseList = findViewById(R.id.btn_course_list);
        btnExamList = findViewById(R.id.btn_exam_list);
        btnProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);
        
        navigationView.setNavigationItemSelectedListener(this);
        
        btnCourseList.setOnClickListener(v -> {
            startActivity(new Intent(this, CourseListActivity.class));
        });
        
        btnExamList.setOnClickListener(v -> {
            startActivity(new Intent(this, ExamListActivity.class));
        });
        
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
        
        btnLogout.setOnClickListener(v -> {
            SpUtil.getInstance(this).clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
    
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            tvTitle.setText("首页");
        } else if (itemId == R.id.nav_course) {
            startActivity(new Intent(this, CourseListActivity.class));
        } else if (itemId == R.id.nav_exam) {
            startActivity(new Intent(this, MyExamActivity.class));
        } else if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_logout) {
            SpUtil.getInstance(this).clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
