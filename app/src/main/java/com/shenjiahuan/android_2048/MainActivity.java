package com.shenjiahuan.android_2048;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.GridLayout.LayoutParams;

import org.w3c.dom.Text;

import java.util.Locale;

import static android.widget.GridLayout.spec;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static SparseIntArray foregroundColor = new SparseIntArray();
    private static SparseIntArray backgroundColor = new SparseIntArray();
    private static SparseIntArray fontSize = new SparseIntArray();
    private double beginX = 0, beginY = 0, endX = 0, endY = 0;
    private boolean enableSwipe = false;
    Grid grid = new Grid();
    TextView[][] text = new TextView[4][4];
    private int[] topMarginRatio;
    private int[] bottomMarginRatio;
    private int[] leftMarginRatio;
    private int[] rightMarginRatio;

    private void onTouchStart(double x, double y) {
        beginX = endX = x;
        beginY = endY = y;
        enableSwipe = true;
    }

    private void onTouchMove(double x, double y) {
        endX = x;
        endY = y;
    }

    private void onTouchEnd() {
        if (Math.pow(endX - beginX, 2) + Math.pow(endY - beginY, 2) > 10 && enableSwipe) {
            String action;
            if (Math.abs(beginX - endX) > Math.abs(beginY - endY)) {
                action = beginX < endX ? "right" : "left";
            } else {
                action = beginY < endY ? "down" : "up";
            }
            if (grid.action(action)) {
                grid.addValue();
                update();
            }
            enableSwipe = false;
        }
    }

    private void loadAnimation(TextView textView, int animation) {
        Animation a = AnimationUtils.loadAnimation(this, animation);
        a.reset();
        textView.clearAnimation();
        textView.startAnimation(a);
    }

    private void setColor(TextView textView, int fg, int bg) {
        GradientDrawable bgShape = (GradientDrawable)textView.getBackground();
        bgShape.setColor(getResources().getColor(bg));
        textView.setTextColor(getResources().getColor(fg));
    }

    private void setText(TextView textView, int num) {
        if (num == 0) {
            textView.setText("");
        } else if (num == -1) {
            textView.setText(R.string.game_over);
        } else {
            textView.setText(String.format(Locale.US, "%d", num));
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.get(num));
    }

    private void updateTextView(TextView textView, int num, String prop) {
        setText(textView, num);
        setColor(textView, foregroundColor.get(num), backgroundColor.get(num));
        if (prop.equals("new")) {
            loadAnimation(textView, R.anim.new_num_animation);
        } else if (prop.equals("merged")) {
            loadAnimation(textView, R.anim.merge_num_animation);
        }
    }

    private void updateScore() {
        TextView currentScore = findViewById(R.id.current_score_value);
        currentScore.setText(String.format(Locale.US, "%d", grid.getScore()));
        TextView maxScore = findViewById(R.id.max_score_value);
        maxScore.setText(String.format(Locale.US, "%d", grid.getScore()));
    }

    private void update() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int num = grid.numbers[i][j].value;
                String prop = grid.numbers[i][j].property;
                updateTextView(text[i][j], num, prop);
            }
        }
        updateScore();
        if (!grid.alive()) {
            GridLayout gameOverGrid = findViewById(R.id.restart_grid);
            TextView gameOver = createTextView();
            LayoutParams params = createLayoutParams(-1, -1);
            gameOverGrid.addView(gameOver, params);
            setText(gameOver, -1);
            setColor(gameOver, foregroundColor.get(-1), backgroundColor.get(-1));
            loadAnimation(gameOver, R.anim.new_num_animation);
        }
    }

    private void addStaticInfo() {
        foregroundColor.put(-1, R.color.color_fg_gameover);
        foregroundColor.put(0, R.color.color_fg_null);
        foregroundColor.put(2, R.color.color_fg_2);
        foregroundColor.put(4, R.color.color_fg_4);
        foregroundColor.put(8, R.color.color_fg_8);
        foregroundColor.put(16, R.color.color_fg_16);
        foregroundColor.put(32, R.color.color_fg_32);
        foregroundColor.put(64, R.color.color_fg_64);
        foregroundColor.put(128, R.color.color_fg_128);
        foregroundColor.put(256, R.color.color_fg_256);
        foregroundColor.put(512, R.color.color_fg_512);
        foregroundColor.put(1024, R.color.color_fg_1024);
        foregroundColor.put(2048, R.color.color_fg_2048);
        backgroundColor.put(-1, R.color.color_bg_gameover);
        backgroundColor.put(0, R.color.color_bg_null);
        backgroundColor.put(2, R.color.color_bg_2);
        backgroundColor.put(4, R.color.color_bg_4);
        backgroundColor.put(8, R.color.color_bg_8);
        backgroundColor.put(16, R.color.color_bg_16);
        backgroundColor.put(32, R.color.color_bg_32);
        backgroundColor.put(64, R.color.color_bg_64);
        backgroundColor.put(128, R.color.color_bg_128);
        backgroundColor.put(256, R.color.color_bg_256);
        backgroundColor.put(512, R.color.color_bg_512);
        backgroundColor.put(1024, R.color.color_bg_1024);
        backgroundColor.put(2048, R.color.color_bg_2048);
        fontSize.put(-1, 48);
        fontSize.put(0, 48);
        fontSize.put(2, 48);
        fontSize.put(4, 48);
        fontSize.put(8, 48);
        fontSize.put(16, 44);
        fontSize.put(32, 44);
        fontSize.put(64, 44);
        fontSize.put(128, 40);
        fontSize.put(256, 40);
        fontSize.put(512, 40);
        fontSize.put(1024, 32);
        fontSize.put(2048, 32);
        Resources res = getResources();
        topMarginRatio = res.getIntArray(R.array.top_margin_ratio);
        bottomMarginRatio = res.getIntArray(R.array.bottom_margin_ratio);
        leftMarginRatio = res.getIntArray(R.array.left_margin_ratio);
        rightMarginRatio = res.getIntArray(R.array.right_margin_ratio);
    }

    private int getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private int getGridWidth() {
        int width = getWidth();
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.width_percent, typedValue, true);
        float width_percent = typedValue.getFloat();
        int gridWidth = (int) (width * width_percent / 100) * 4;
        for (int _i = 0; _i < 4; ++_i) {
            gridWidth += width * leftMarginRatio[_i] / 100 + width * rightMarginRatio[_i] / 100;
        }
        return gridWidth;
    }

    private int getGridHeight() {
        int width = getWidth();
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.height_percent, typedValue, true);
        float height_percent = typedValue.getFloat();
        int gridHeight = (int) (width * height_percent / 100) * 4;
        for (int _i = 0; _i < 4; ++_i) {
            gridHeight += width * topMarginRatio[_i] / 100 + width * bottomMarginRatio[_i] / 100;
        }
        return gridHeight;
    }

    private TextView createTextView() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/helvetica-bold.ttf");
        TextView textView = new TextView(this);
        textView.setTypeface(font);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.rounded_corner);
        return textView;
    }

    private LayoutParams createLayoutParams(int i, int j) {
        GridLayout.Spec rowSpec = spec(i != -1 ? i : 0, GridLayout.CENTER);
        GridLayout.Spec colSpec = spec(j != -1 ? j : 0, GridLayout.CENTER);
        int width = getWidth();
        LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.height_percent, typedValue, true);
        float height_percent = typedValue.getFloat();
        getResources().getValue(R.dimen.width_percent, typedValue, true);
        float width_percent = typedValue.getFloat();
        if (i != -1 && j != -1) {
            params.height = (int) (width * height_percent / 100);
            params.width = (int) (width * width_percent / 100);
            params.topMargin = width * topMarginRatio[i] / 100;
            params.bottomMargin = width * bottomMarginRatio[i] / 100;
            params.leftMargin = width * leftMarginRatio[j] / 100;
            params.rightMargin = width * rightMarginRatio[j] / 100;
        } else {
            params.height = getGridHeight();
            params.width = getGridWidth();
        }
        return params;
    }

    private LayoutParams createScoreBoardLayoutParams(int j) {
        int width = getWidth();
        int gridWidth = getGridWidth();
        int margin = (width - gridWidth) / 2;
        GridLayout.Spec rowSpec = spec(0, GridLayout.CENTER);
        GridLayout.Spec colSpec = spec(j, GridLayout.CENTER);
        LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.scoreboard_height_percent, typedValue, true);
        float scoreboardHeightPercent = typedValue.getFloat();
        getResources().getValue(R.dimen.scoreboard_width_percent, typedValue, true);
        float scoreboardWidthPercent = typedValue.getFloat();
        params.height = (int) (width * scoreboardHeightPercent / 100);
        params.width = (int) (width * scoreboardWidthPercent / 100);
        params.setMargins(margin, 0, 0, 0);
        return params;
    }

    private void initScoreBoard() {
        GridLayout scoreBoard = findViewById(R.id.score_board);
        int width = getWidth();
        int gridWidth = getGridWidth();
        int x = (width - gridWidth) / 2;
        int y = (int)(scoreBoard.getY() - (width - gridWidth) / 2);
        scoreBoard.setX(x);
        scoreBoard.setY(y);
        LinearLayout currentScore = findViewById(R.id.current_score_board);
        LayoutParams currentScoreParams = createScoreBoardLayoutParams(0);
        currentScore.setLayoutParams(currentScoreParams);
        LinearLayout maxScore = findViewById(R.id.max_score_board);
        LayoutParams maxScoreParams = createScoreBoardLayoutParams(1);
        maxScore.setLayoutParams(maxScoreParams);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: start");
        GridLayout gridLayout = findViewById(R.id.grid_layout);
        addStaticInfo();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                TextView textView = createTextView();
                LayoutParams params = createLayoutParams(i, j);
                gridLayout.addView(textView, params);
                text[i][j] = textView;
            }
        }
        initScoreBoard();
        update();
        gridLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                double x = event.getX(), y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onTouchStart(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        onTouchMove(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        onTouchEnd();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
