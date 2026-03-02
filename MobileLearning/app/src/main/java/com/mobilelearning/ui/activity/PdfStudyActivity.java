package com.mobilelearning.ui.activity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.LearningApi;
import com.mobilelearning.utils.RetrofitUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PdfStudyActivity extends AppCompatActivity {
    
    private ImageView ivPdfPage;
    private TextView tvPageInfo;
    private Button btnPrev;
    private Button btnNext;
    private Button btnComplete;
    private TextView tvSectionName;
    
    private Handler handler = new Handler(Looper.getMainLooper());
    private long sectionId;
    private int currentPage = 0;
    private int totalPages = 0;
    private int pdfReadTime;
    private long startTime;
    private boolean isStarted = false;
    
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPageRender;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_study);
        
        sectionId = getIntent().getLongExtra("sectionId", 0);
        String sectionName = getIntent().getStringExtra("sectionName");
        String pdfUrl = getIntent().getStringExtra("pdfUrl");
        pdfReadTime = getIntent().getIntExtra("pdfReadTime", 300);
        
        tvSectionName = findViewById(R.id.tv_section_name);
        ivPdfPage = findViewById(R.id.iv_pdf_page);
        tvPageInfo = findViewById(R.id.tv_page_info);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnComplete = findViewById(R.id.btn_complete);
        
        if (sectionName != null) {
            tvSectionName.setText(sectionName);
        }
        
        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                renderPage(currentPage);
            }
        });
        
        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                renderPage(currentPage);
            }
        });
        
        btnComplete.setOnClickListener(v -> {
            long readTime = (System.currentTimeMillis() - startTime) / 1000;
            if (readTime >= pdfReadTime) {
                saveProgressAndFinish();
            } else {
                int remaining = pdfReadTime - (int)readTime;
                Toast.makeText(this, "请继续阅读" + remaining + "秒", Toast.LENGTH_SHORT).show();
            }
        });
        
        startTime = System.currentTimeMillis();
        
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            loadPdfFromUrl(pdfUrl);
        } else {
            Toast.makeText(this, "PDF 地址为空", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveProgressAndFinish() {
        updatePdfProgress();
        Toast.makeText(this, "学习完成，进度已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void loadPdfFromUrl(String pdfUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);
                connection.connect();
                
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(PdfStudyActivity.this, 
                        "PDF 下载失败：" + responseCode, Toast.LENGTH_LONG).show());
                    return;
                }
                
                File pdfFile = new File(getCacheDir(), "temp.pdf");
                FileOutputStream fos = new FileOutputStream(pdfFile);
                
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
                
                runOnUiThread(() -> openPdf(pdfFile));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(PdfStudyActivity.this, 
                    "PDF 加载失败：" + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    
    private void openPdf(File pdfFile) {
        try {
            android.os.ParcelFileDescriptor parcelFileDescriptor = 
                android.os.ParcelFileDescriptor.open(pdfFile, android.os.ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            totalPages = pdfRenderer.getPageCount();
            
            if (totalPages > 0) {
                tvPageInfo.setText("第 1 / " + totalPages + " 页");
                renderPage(0);
            } else {
                Toast.makeText(this, "PDF 无页面", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "PDF 打开失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void renderPage(int pageNumber) {
        if (pdfRenderer == null || pageNumber >= totalPages || pageNumber < 0) return;
        
        if (currentPageRender != null) {
            currentPageRender.close();
        }
        
        currentPageRender = pdfRenderer.openPage(pageNumber);
        Bitmap bitmap = Bitmap.createBitmap(
            currentPageRender.getWidth(),
            currentPageRender.getHeight(),
            Bitmap.Config.ARGB_8888
        );
        
        currentPageRender.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        ivPdfPage.setImageBitmap(bitmap);
        
        tvPageInfo.setText("第 " + (pageNumber + 1) + " / " + totalPages + " 页");
        
        btnPrev.setEnabled(pageNumber > 0);
        btnNext.setEnabled(pageNumber < totalPages - 1);
        
        if (!isStarted) {
            isStarted = true;
            handler.postDelayed(this::updatePdfProgress, 5000);
        }
    }
    
    private void updatePdfProgress() {
        LearningApi api = RetrofitUtil.create(LearningApi.class);
        api.updatePdfProgress(sectionId, currentPage + 1, !isStarted).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {}
            
            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {}
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentPageRender != null) {
            currentPageRender.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        updatePdfProgress();
    }
    
    @Override
    public void onBackPressed() {
        updatePdfProgress();
        super.onBackPressed();
    }
}
