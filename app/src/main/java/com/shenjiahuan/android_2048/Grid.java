package com.shenjiahuan.android_2048;

import android.util.Log;

import java.util.*;

class Pair<X, Y> {
    final X x;
    final Y y;
    Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

class Number {
    int value;
    String property;
    Number() {
        this.value = 0;
        this.property = "";
    }

    Number(int value, String property) {
        this.value = value;
        this.property = property;
    }
}

class Transpose {
    static Number[][] upLeftTranspose(Number[][] numbers) {
        Number[][] copy = new Number[numbers[0].length][numbers.length];
        for (int i = 0; i < numbers.length; ++i) {
            for (int j = 0; j < numbers[i].length; ++j) {
                copy[j][i] = numbers[i][j];
            }
        }
        return copy;
    }

    static Number[][] upDownTranspose(Number[][] numbers) {
        Number[][] copy = new Number[numbers.length][numbers[0].length];
        for (int i = 0; i < numbers.length; ++i) {
            copy[i] = numbers[numbers.length - i - 1];
        }
        return copy;
    }

    static Number[][] leftRightTranspose(Number[][] numbers) {
        numbers = Transpose.upLeftTranspose(numbers);
        numbers = Transpose.upDownTranspose(numbers);
        numbers = Transpose.upLeftTranspose(numbers);
        return numbers;
    }
}

class Grid {
    private static final String TAG = "Grid";
    Number[][] numbers = new Number[4][4];
    private int score = 0;
    private final static Random generator = new Random();
    
    Grid() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                numbers[i][j] = new Number();
            }
        }
        for (int i = 0; i < 2; ++i) {
            addValue();
        }
    }
    
    private static Pair<Integer, Integer> getRandom() {
        int row = generator.nextInt(4);
        int col = generator.nextInt(4);
        return new Pair<>(row, col);
    }

    void addValue() {
        boolean found = false;
        while (!found) {
            Pair<Integer, Integer> p = Grid.getRandom();
            if (numbers[p.x][p.y].value == 0) {
                numbers[p.x][p.y] = new Number(generator.nextDouble() > 0.5 ? 4 : 2, "new");
                found = true;
            }
        }
    }

    private boolean move(int rowNum) {
        boolean isMoved = false;
        int nextj = 0;
        for (int j = 0; j < 3; j = nextj) {
            nextj = j + 1;
            if (numbers[rowNum][j].value != 0) {
                continue;
            }
            for (int k = j; k < 3; ++k) {
                this.numbers[rowNum][k] = this.numbers[rowNum][k + 1];
                if (this.numbers[rowNum][k].value != 0) {
                    isMoved = true;
                    nextj = j;
                }
            }
            this.numbers[rowNum][3] = new Number();
        }
        return isMoved;
    }

    private boolean merge(int rowNum) {
        boolean isMerged = false;
        for (int j = 0; j <= 2; ++j) {
            if (numbers[rowNum][j].value != 0 && numbers[rowNum][j + 1].value != 0 &&
                    numbers[rowNum][j].value == numbers[rowNum][j + 1].value) {
                numbers[rowNum][j].value *= 2;
                numbers[rowNum][j].property = "merged";
                numbers[rowNum][j + 1] = new Number();
                isMerged = true;
                score += numbers[rowNum][j].value;
            }
        }
        return isMerged;
    }

    private boolean leftMoveAndMerge() {
        boolean isChanged = false;
        for (int i = 0; i < 4; ++i) {
            boolean action1 = move(i);
            boolean action2 = merge(i);
            boolean action3 = move(i);
            isChanged = isChanged || action1 || action2 || action3;
        }
        return isChanged;
    }

    private void resetIsNew() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                numbers[i][j].property = "";
            }
        }
    }

    private void transpose(String direction, boolean reset) {
        switch (direction) {
            case "up":
                numbers = Transpose.upLeftTranspose(numbers);
                break;
            case "down":
                if (reset) {
                    numbers = Transpose.upLeftTranspose(numbers);
                    numbers = Transpose.upDownTranspose(numbers);
                } else {
                    numbers = Transpose.upDownTranspose(numbers);
                    numbers = Transpose.upLeftTranspose(numbers);
                }
                break;
            case "right":
                numbers = Transpose.leftRightTranspose(numbers);
                break;
            default:
                break;
        }
    }

    boolean action(String direction) {
        Log.d(TAG, "action: " + direction);
        resetIsNew();
        transpose(direction, false);
        boolean isChanged = leftMoveAndMerge();
        transpose(direction, true);
        return isChanged;
    }

    boolean alive() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (numbers[i][j].value == 0) {
                    return true;
                }
                if (i < 3 && numbers[i + 1][j].value != 0 && numbers[i][j].value == numbers[i + 1][j].value) {
                    return true;
                }
                if (j < 3 && numbers[i][j + 1].value != 0 && numbers[i][j].value == numbers[i][j + 1].value) {
                    return true;
                }
            }
        }
        return false;
    }
}
