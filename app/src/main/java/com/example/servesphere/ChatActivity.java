package com.example.servesphere;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.servesphere.room.AppDatabase;
import com.example.servesphere.room.BookingDao;

public class ChatActivity extends AppCompatActivity {

    private EditText inputMessage;
    private Button sendButton;
    private LinearLayout chatContainer;
    private ScrollView chatScroll;

    private ChatCoordinator chatCoordinator;
    private String userId = "user123"; // demo user id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        chatContainer = findViewById(R.id.chatContainer);
        chatScroll = findViewById(R.id.chatScroll);

        // Repos (local Room + simple stubs)
        BookingDao bookingDao = AppDatabase.getInstance(this).bookingDao();
        BookingRepository bookingRepo = new BookingRepository(bookingDao);
        ServicesRepository servicesRepo = new ServicesRepository(this);
        MapsRepository mapsRepo = new MapsRepository(this);

        // Gemini service
        GeminiApiService geminiService = GeminiClient.getService();

        // Coordinator
        chatCoordinator = new ChatCoordinator(
                this,
                bookingRepo,
                servicesRepo,
                mapsRepo,
                geminiService,
                BuildConfig.GEMINI_API_KEY // pulled from local.properties via BuildConfig
        );

        sendButton.setOnClickListener(v -> {
            String userText = inputMessage.getText().toString().trim();
            if (userText.isEmpty()) {
                Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            addMessage("You: " + userText);
            inputMessage.setText("");

            double lat = 19.0760;  // Mumbai (demo)
            double lng = 72.8777;

            chatCoordinator.handleMessage(userId, userText, lat, lng, new ChatCoordinator.Callback() {
                @Override
                public void onResult(String reply) {
                    runOnUiThread(() -> {
                        addMessage("ServeSphere: " + reply);
                        scrollToBottom();
                    });
                }

                @Override
                public void onError(Throwable t) {
                    runOnUiThread(() -> {
                        addMessage("⚠️ Error: " + t.getMessage());
                        scrollToBottom();
                        Log.e("ChatActivity", "Gemini error", t);
                    });
                }
            });
        });
    }

    private void addMessage(String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setPadding(16, 8, 16, 8);
        chatContainer.addView(tv);
    }

    private void scrollToBottom() {
        chatScroll.post(() -> chatScroll.fullScroll(View.FOCUS_DOWN));
    }
}
