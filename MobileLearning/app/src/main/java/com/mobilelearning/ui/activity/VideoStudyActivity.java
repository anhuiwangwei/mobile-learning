package com.mobilelearning.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.LearningApi;
import com.mobilelearning.utils.RetrofitUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoStudyActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    
    private VideoView videoView;
    private SeekBar seekBar;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private Button btnPlayPause;
    private TextView tvSectionName;
    
    private Handler handler = new Handler(Looper.getMainLooper());
    private long sectionId;
    private boolean isAllowSeek;
    private boolean isStepLearning;
    private boolean isPlaying = false;
    private int currentProgress = 0;
    private int watchTime = 0;
    
    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoView != null && isPlaying) {
                int currentPosition = videoView.getCurrentPosition();
                int duration = videoView.getDuration();
                if (duration > 0) {
                    currentProgress = (currentPosition * 100) / duration;
                    seekBar.setProgress(currentProgress);
                    tvCurrentTime.setText(formatTime(currentPosition));
                    tvTotalTime.setText(formatTime(duration));
                    watchTime = currentPosition / 1000;
                }
                handler.postDelayed(this, 1000);
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_study);
        
        sectionId = getIntent().getLongExtra("sectionId", 0);
        String sectionName = getIntent().getStringExtra("sectionName");
        String videoUrl = getIntent().getStringExtra("videoUrl");
        isAllowSeek = getIntent().getBooleanExtra("isAllowSeek", true);
        isStepLearning = getIntent().getBooleanExtra("isStepLearning", false);
        
        tvSectionName = findViewById(R.id.tv_section_name);
        videoView = findViewById(R.id.video_view);
        seekBar = findViewById(R.id.seek_bar);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        
        if (sectionName != null) {
            tvSectionName.setText(sectionName);
        }
        
        if (!isAllowSeek) {
            seekBar.setEnabled(false);
            Toast.makeText(this, "当前视频禁止快进", Toast.LENGTH_SHORT).show();
        }
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isAllowSeek) {
                    int duration = videoView.getDuration();
                    int newPosition = (duration * progress) / 100;
                    videoView.seekTo(newPosition);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                videoView.pause();
                btnPlayPause.setText("▶ 播放");
            } else {
                videoView.start();
                btnPlayPause.setText("⏸ 暂停");
            }
            isPlaying = !isPlaying;
        });
        
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(mp -> {
            isPlaying = false;
            btnPlayPause.setText("▶ 播放");
            updateVideoProgress(100, watchTime);
            Toast.makeText(VideoStudyActivity.this, "视频播放完成", Toast.LENGTH_SHORT).show();
        });
        
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(VideoStudyActivity.this, "视频播放错误", Toast.LENGTH_SHORT).show();
            return true;
        });
        
        // 加载视频
        try {
            if (videoUrl != null && !videoUrl.isEmpty()) {
                Uri uri = Uri.parse(videoUrl);
                videoView.setVideoURI(uri);
            } else {
                Toast.makeText(this, "视频地址为空", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "视频加载失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(false);
        seekBar.setMax(mp.getDuration());
        isPlaying = true;
        videoView.start();
        btnPlayPause.setText("⏸ 暂停");
        handler.post(updateProgressRunnable);
    }
    
    private void updateVideoProgress(int progress, int watchTime) {
        LearningApi api = RetrofitUtil.create(LearningApi.class);
        api.updateVideoProgress(sectionId, progress, watchTime).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VideoStudyActivity.this, "进度已保存", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                // 保存失败不提示，避免打扰用户
            }
        });
    }
    
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateProgressRunnable);
        if (videoView != null) {
            videoView.stopPlayback();
        }
        updateVideoProgress(currentProgress, watchTime);
    }
    
    @Override
    public void onBackPressed() {
        updateVideoProgress(currentProgress, watchTime);
        super.onBackPressed();
    }
}
