package com.jianzhang.TextEditor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;

public class TextEditor {

    private PriorityQueue<String[]> minHeap;
    private Deque<String[]> undoStack;
    private Deque<String[]> redoStack;
    private String curString;
    private int[] curSelection = new int[2];

    TextEditor(String[][] input) {
        undoStack = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
        curString = "";
        minHeap = new PriorityQueue<>(11, (o1, o2) -> {
            if (o1[0].equals(o2[0])) {
                return 0;
            }
            return o1[0].compareTo(o2[0]);
        });
        sortInput(input);
    }

    private void sortInput(String[][] input) {
        for (String[] array : input) {
            minHeap.offer(array);
        }
    }

    public void runEditor() {
        while (!minHeap.isEmpty()) {
            String[] curOperation = minHeap.poll();
            executeOperation(curOperation);
        }
    }

    private void executeOperation(String[] curOperation) {
        switch (curOperation[1]) {
            case "APPEND":
                append(curOperation[2]);
                undoStack.offerFirst(curOperation);
                clearRedoStack();
                break;
            case "BACKSPACE":
                backspace();
                undoStack.offerFirst(curOperation);
                clearRedoStack();
                break;
            case "UNDO":
                undo();
                break;
            case "REDO":
                redo();
                break;
            case "SELECT":
                select(Integer.parseInt(curOperation[2]), Integer.parseInt(curOperation[3]));
                break;
            case "BOLD":
                bold();
                break;
        }
    }

    private void clearRedoStack() {
        redoStack.clear();
    }

    private boolean hasSelection() {
        return curSelection[0] != 0 && curSelection[1] != 0;
    }

    private void resetSelectionPointers() {
        curSelection[0] = 0;
        curSelection[1] = 0;
    }

    private void append(String input) {
        StringBuilder sb = new StringBuilder();
        if (!hasSelection()) {
            sb.append(curString).append(input);  // if there is no selection, directly append input to the end
        } else {                                 // else, use input to replace the selected range
            sb.append(curString, 0, curSelection[0]).append(input).append(curString.substring(curSelection[1]));
        }
        curString = sb.toString();
        resetSelectionPointers();
    }

    private void backspace() {
        if (curString.length() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (!hasSelection()) {
            sb.append(curString, 0, curString.length() - 2);
        } else {
            sb.append(curString, 0, curSelection[0]).append(curString.substring(curSelection[1]));
        }
        curString = sb.toString();
        resetSelectionPointers();
    }

    private void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        String[] lastOperation = undoStack.pollFirst();
        redoStack.offerFirst(lastOperation);
        // TODO: undo the string to the state before the last operation
    }

    private void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        String[] curOperation = redoStack.pollFirst();
        executeOperation(curOperation);
    }

    private void select(int start, int end) {
        if (start < 0) {
            return;
        }
        if (end > curString.length() - 1) {
            end = curString.length() - 1;
        }
        curSelection[0] = start;
        curSelection[1] = end;
    }

    private void bold() {
        if (!hasSelection() || curString.length() == 0) {
            return;
        }
        // TODO: How to do bold?
    }

    public static void main(String[] args) {
        String[][] input = new String[1][3];
        String[] one = {"1", "APPEND", "hello"};
        input[0] = one;
        TextEditor textEditor = new TextEditor(input);
    }
}
