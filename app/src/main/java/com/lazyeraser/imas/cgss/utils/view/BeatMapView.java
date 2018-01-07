package com.lazyeraser.imas.cgss.utils.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lazyeraser.imas.cgss.entity.Note;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.derehelper.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lazyeraser on 2017/12/28.
 * music beatmap
 */

public class BeatMapView extends View {

    private List<Note> data;
    private Paint mBitPaint;

    private Bitmap note_0;
    private Bitmap note_1;
    private Bitmap note_2;
    private Bitmap note_3;
    private Bitmap note_4;

    private int width;
    private int height;
    private int totalSec;

    private LinkedHashMap<Integer, List<Note>> groupMap = new LinkedHashMap<>();
    private LinkedHashMap<Float, List<Note>> syncMap = new LinkedHashMap<>();
    private Map<Note, float[]> noteXYMap = new HashMap<>();

    private final static int oneSecY = 600;
    private final static float margin = 0.1f;
    private final static float noteDis = (1 - (2 * margin)) / 4;

    public BeatMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

        note_0 = Utils.readBitMap(context, R.drawable.ic_note_0);
        note_1 = Utils.readBitMap(context, R.drawable.ic_note_1);
        note_2 = Utils.readBitMap(context, R.drawable.ic_note_2);
        note_3 = Utils.readBitMap(context, R.drawable.ic_note_3);
        note_4 = Utils.readBitMap(context, R.drawable.ic_note_4);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (data == null)
            return;
        width = MeasureSpec.getSize(widthMeasureSpec);
        Note lastNote = data.get(data.size() - 1);
        totalSec = (int) Math.rint(lastNote.sec);
        height = totalSec * oneSecY;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null)
            return;
        canvas.drawColor(0xFF233333); // background
        drawGrid(canvas);
        drawNotes(canvas);
    }


    private void drawGrid(Canvas canvas) {
        mBitPaint.setStrokeWidth(5);
        mBitPaint.setColor(0x50BABABA);

        // note track
        for (int i = 1; i <= 5; i++) {
            canvas.drawLine(
                    getPointX(i), 0,
                    getPointX(i), height,
                    mBitPaint);
        }

        // time line
        mBitPaint.setTextSize(width * 0.03f);
        mBitPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = totalSec; i >= 0; i--) {
            canvas.drawLine(
                    0, oneSecY * i,
                    width, oneSecY * i,
                    mBitPaint);
            int sec = totalSec - i;
            int min = sec / 60;
            canvas.drawText(String.format("%02d:%02d", min, sec - (60 * min)), width, (oneSecY * i) - 5, mBitPaint);
        }
    }

    private void drawNotes(Canvas canvas) {
        List<Note> notesToMatch = new ArrayList<>();
        AllNotes:
        for (Note note : data) {
            if (note.sec == 0) {
                continue;
            }
            // coordinate
            float[] noteXY = getNoteXY(note, getNotePic(note));
            noteXYMap.put(note, noteXY);

            // grouping
            if (note.groupId != 0) {
                groupNote(groupMap, note, note.groupId);
            }
            if (note.sync == 1) {
                groupNote(syncMap, note, note.sec);
            }
            boolean longPress = note.status == 0 && note.type != 1;
            boolean verticalLongPress = longPress && note.groupId == 0;
            boolean movingLongPress = longPress && note.groupId != 0;

            for (Note noteToM : notesToMatch) {
                if (!movingLongPress && noteToM.finishPos == note.finishPos) {
                    groupNote(groupMap, note, Integer.MAX_VALUE - noteToM.id);
                    notesToMatch.remove(noteToM);
                    continue AllNotes;
                }
            }
            if (verticalLongPress) {
                // start a new one
                groupNote(groupMap, note, Integer.MAX_VALUE - note.id);
                notesToMatch.add(note);
            }

        }
        // connect line
        mBitPaint.setStrokeWidth(20);
        mBitPaint.setColor(0xCFBABABA);
        drawConnectLine(canvas, groupMap);
        groupMap.clear();

        // sync line
        mBitPaint.setStrokeWidth(8);
        mBitPaint.setColor(0xCFBABABA);
        drawConnectLine(canvas, syncMap);
        syncMap.clear();

        // draw note pic
        for (Note note : noteXYMap.keySet()) {
            canvas.drawBitmap(getNotePic(note), noteXYMap.get(note)[0], noteXYMap.get(note)[1], null);
        }
    }

    private <T> void drawConnectLine(Canvas canvas, Map<T, List<Note>> map) {
        for (List<Note> notes : map.values()) {
            for (int i = 0; i < notes.size() - 1; i++) {
                float[] noteXY_start = noteXYMap.get(notes.get(i));
                float[] noteXY_end = noteXYMap.get(notes.get(i + 1));
                canvas.drawLine(
                        noteXY_start[2], noteXY_start[3],
                        noteXY_end[2], noteXY_end[3],
                        mBitPaint);
            }
        }
    }


    private float getPointX(int i) { // i: 1 ~ 5
        return (margin + (--i * noteDis)) * width;
    }

    private <T> void groupNote(Map<T, List<Note>> map, Note note, T key) {
        if (!map.containsKey(key)) {
            List<Note> groupNotes = new ArrayList<>();
            groupNotes.add(note);
            map.put(key, groupNotes);
        } else {
            map.get(key).add(note);
        }
    }

    private Bitmap getNotePic(Note note) {
        Bitmap notePic;
        switch (note.status) {
            case 0:
                if (note.type == 1) {
                    notePic = note_0;
                } else if (note.groupId != 0) {
                    notePic = note_4; // long press with move (slide
                } else {
                    notePic = note_1; // long press
                }
                break;
            case 1: // left flick
                notePic = note_2;
                break;
            case 2: // right flick
                notePic = note_3;
                break;
            default:
                notePic = note_0;
                break;

        }
        return notePic;
    }

    private float[] getNoteXY(Note note, Bitmap notePic) {
        float[] result = new float[4];
        // coordinate for line
        result[2] = getPointX(note.finishPos);
        result[3] = oneSecY * (totalSec - note.sec);
        // coordinate for note pic
        result[0] = result[2] - notePic.getWidth() / 2;
        result[1] = result[3] - notePic.getHeight() / 2;
        return result;
    }

    public int getFistNoteY(){
        Note firstNote = data.get(2);
        Note lastNote = data.get(data.size() - 1);
        return (int)(oneSecY * (lastNote.sec - firstNote.sec));
    }

    public void setData(@NonNull List<Note> data) {
        this.data = data;
        requestLayout();
        invalidate();
    }

    public void setData(@NonNull String sData) {
        List<Note> list = new ArrayList<>();
        String t = sData.replace("id,sec,type,startPos,finishPos,status,sync,groupId\n", "");
        for (String s : t.split("\n")) {
            list.add(new Note(s));
        }
        setData(list);
    }

    public List<Note> getData() {
        return data;
    }
}
