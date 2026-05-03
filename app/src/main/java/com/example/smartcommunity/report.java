package com.example.smartcommunity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class report extends AppCompatActivity {

    EditText titleInput, descriptionInput, locationInput;
    Spinner categorySpinner;
    Button submitBtn, uploadBtn;
    ImageView imagePreview;

    FirebaseAuth mAuth;
    DatabaseReference reportsRef;

    Uri imageUri;
    String uploadedImageUrl = "";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        imagePreview = findViewById(R.id.imgPreview);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        locationInput = findViewById(R.id.locationInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitBtn = findViewById(R.id.submitBtn);
        uploadBtn = findViewById(R.id.uploadBtn);

        mAuth = FirebaseAuth.getInstance();
        reportsRef = FirebaseDatabase.getInstance().getReference("reports");

        initCloudinary();

        String[] categories = {"Theft", "Accident", "Suspicious Activity", "Emergency"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        categorySpinner.setAdapter(adapter);

        submitBtn.setOnClickListener(v -> saveReport());
        uploadBtn.setOnClickListener(v -> showImageOptions());
    }


    // all methods
    private void initCloudinary() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "djl9vbvp1");
            config.put("secure", true);
            MediaManager.init(this, config);
        } catch (IllegalStateException e) {
            // already initialized
        } catch (Exception e) {
            Toast.makeText(this, "Cloudinary init failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //show image options
    private void showImageOptions() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            return;
        }

        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                photoFile
        );

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "SMARTCOMMUNITY_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show();
            uploadImageToCloudinary();
        }

        if (requestCode == CAMERA_REQUEST) {
            imagePreview.setImageURI(imageUri);
            Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show();
            uploadImageToCloudinary();
        }
    }

    private void uploadImageToCloudinary() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select or capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaManager.get().upload(imageUri)
                .unsigned("smartcommunity_unsigned")
                .option("folder", "smartcommunity/reports")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(report.this, "Uploading image...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Object secureUrl = resultData.get("secure_url");
                        if (secureUrl != null) {
                            uploadedImageUrl = secureUrl.toString();
                            Toast.makeText(report.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(report.this, "Upload finished but URL not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(report.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                })
                .dispatch();
    }


    private void saveReport() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String priority = detectPriority(category, description);

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uploadedImageUrl.isEmpty()) {
            Toast.makeText(this, "Please upload an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String reportId = reportsRef.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();

        if (reportId == null) {
            Toast.makeText(this, "Error generating ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("fullName").get().addOnSuccessListener(snapshot -> {
            String reporterName = snapshot.getValue(String.class);

            if (reporterName == null || reporterName.trim().isEmpty()) {
                reporterName = "Anonymous";
            }



            ReportModel report = new ReportModel(
                    reportId,
                    userId,
                    reporterName,
                    title,
                    description,
                    location,
                    category,
                    uploadedImageUrl,
                    "Pending",
                    priority,
                    System.currentTimeMillis()
            );

            reportsRef.child(reportId).setValue(report)
                    .addOnSuccessListener(unused -> {
                        createNotification(userId,
                                "Report Submitted",
                                "Your report" + "\n" + title + "\n" + "under " + category + " category has been submitted successfully.");
                        createUserLog(
                                userId,
                                "Submitted Report",
                                "Report title: " + title + " | Category: " + category
                        );
                        Toast.makeText(this, "Report Submitted!", Toast.LENGTH_SHORT).show();
                        clearFields();
                        finish();
                    })

                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to get user name: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

    private void createNotification(String userId, String title, String message) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("notifications");
        String notificationId = notifRef.push().getKey();

        if (notificationId == null) return;

        NotificationModel notification = new NotificationModel(
                notificationId,
                userId,
                title,
                message,
                System.currentTimeMillis(),
                false
        );

        notifRef.child(notificationId).setValue(notification);
    }


    //preset risk detector
    private String detectPriority(String category, String description) {
        String desc = description.toLowerCase();

        if (category.equalsIgnoreCase("Emergency")
                || desc.contains("help")
                || desc.contains("danger")
                || desc.contains("blood")
                || desc.contains("fire")
                || desc.contains("hurt")
                || desc.contains("weapon")
                || desc.contains("attack")) {
            return "HIGH";
        }

        if (category.equalsIgnoreCase("Suspicious Activity")
                || desc.contains("suspicious")
                || desc.contains("following")
                || desc.contains("stalking")
                || desc.contains("unknown person")
                || desc.contains("threat")) {
            return "MEDIUM";
        }

        return "LOW";
    }


    private void clearFields() {
        titleInput.setText("");
        descriptionInput.setText("");
        locationInput.setText("");
        categorySpinner.setSelection(0);
        imagePreview.setImageDrawable(null);
        imageUri = null;
        uploadedImageUrl = "";
    }

    //creating logs
    private void createUserLog(String userId, String action, String details) {
        DatabaseReference logsRef = FirebaseDatabase.getInstance().getReference("user_logs");
        String logId = logsRef.push().getKey();

        if (logId == null) return;

        UserLogModel log = new UserLogModel(
                logId,
                userId,
                action,
                details,
                System.currentTimeMillis()
        );

        logsRef.child(logId).setValue(log);
    }
}
