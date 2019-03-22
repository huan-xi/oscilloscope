
    /**
    OsciPrime an Open Source Android Oscilloscope
    Copyright (C) 2012  Manuel Di Cerbo, Nexus-Computing GmbH Switzerland
    Copyright (C) 2012  Andreas Rudolf, Nexus-Computing GmbH Switzerland

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    */

package ch.nexuscomputing.android.osciprimeics.colorpicker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import ch.nexuscomputing.android.osciprimeics.L;
import ch.nexuscomputing.android.osciprimeics.OsciPrimeICSActivity;

public class ColorPicker extends View {

	private ColorListener mListener;
	private float mOffsetX;
	private float mOffsetY;
	private static float SIZE = 500;
	static float DIV240 = 50;//for 240dp
	static float DIV = 50;
	static float DENSITY;

	public interface ColorListener {
		void colorChanged(int c);
	}

	public ColorPicker(OsciPrimeICSActivity context, ColorListener l) {
		super(context);
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		DENSITY = dm.densityDpi;
		L.d("density " +DENSITY);
		DIV = DIV240*DENSITY/240f;
		mListener = l;
		setWillNotDraw(false);

		final GestureDetector gd = new GestureDetector(context,
				new SimpleOnGestureListener() {
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						offset(e1, e2, distanceX, distanceY);
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						float W = DIV;//getWidth() / 16.0f;
						float x = e.getX() -mOffsetX;
						float y = e.getY() -mOffsetY;
						int r = (int) ((y - y % W) / W);
						int c = (int) ((x - x % W) / W);

						r = Math.min(15, r);
						c = Math.min(15, c);
						L.d("row: " + r + " col: " + c);
						mListener.colorChanged(Color.rgb(COLOR_PALETTED[16 * r
								+ c][0], COLOR_PALETTED[16 * r + c][1],
								COLOR_PALETTED[16 * r + c][2]));
						return true;
					}

				});
		this.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gd.onTouchEvent(event);
				invalidate();
				return true;
			}
		});
	}

	private void offset(MotionEvent e1, MotionEvent e2, float x,
			float y) {
		if(W < SIZE || H < SIZE){
			mOffsetX += -x ;
			mOffsetY += -y ;
			
			mOffsetX = Math.min(mOffsetX, 0);
			mOffsetY = Math.min(mOffsetY, 0);

			if(W > H){
				mOffsetX = Math.max(mOffsetX, -(SIZE-H));
				mOffsetY = Math.max(mOffsetY, -(SIZE-H));
			}else{
				mOffsetX = Math.max(mOffsetX, -(SIZE-W));
				mOffsetY = Math.max(mOffsetY, -(SIZE-W));
			}
		}
		
		L.d("offset "+mOffsetX+" widthsss: "+W);
		L.d("offset "+mOffsetY+" heigthsss: "+H);
	}
	

	public static int sSelectedColor = Color.WHITE;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		SIZE=DIV*16;
		
		W = ((RelativeLayout)getParent().getParent()).getWidth(); H = ((RelativeLayout)getParent().getParent()).getHeight();
		
		L.d("width: "+W);
		if(W > H){
			if(SIZE >= H)
				setMeasuredDimension(H, H);
			else{
				setMeasuredDimension((int)(SIZE), (int)(SIZE));
				H = (int) (SIZE);
				W = (int) (SIZE);
			}
		}else{
			if(DIV240*16 >= W)
				setMeasuredDimension(W, W);
			else{
				setMeasuredDimension((int)(SIZE), (int)(SIZE));
				H = (int) (SIZE);
				W = (int) (SIZE);
			}
			
		}
	}
	
	
	int W = 0;
	int H = 0;
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		SIZE = 16*DIV;
		//W = ((LinearLayout)getParent()).getWidth(); H = ((LinearLayout)getParent()).getHeight();
		W = ((RelativeLayout)getParent().getParent()).getWidth(); H = ((RelativeLayout)getParent().getParent()).getHeight();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	RectF mRect = new RectF();
	Paint mPaint = new Paint();
	
	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.reset();
		canvas.drawColor(Color.WHITE);
		canvas.translate(mOffsetX, mOffsetY);
		
		mPaint.setStyle(Style.FILL);
		L.d(canvas.getDensity());
		float W = DIV;//getWidth() / 16.0f;

		int row = -1;
		for (int i = 0; i < COLOR_PALETTED.length; i++) {
			if (i % 16 == 0)
				row++;
			mRect.set((i % 16) * W, row * W, ((i % 16) + 1) * W, (row + 1) * W);
			mPaint.setColor(Color.rgb(COLOR_PALETTED[i][0],
					COLOR_PALETTED[i][1], COLOR_PALETTED[i][2]));
			canvas.drawRect(mRect, mPaint);
		}
		
		canvas.translate(-mOffsetX, -mOffsetY);
		super.onDraw(canvas);
	}

	public static final int [][] COLOR_PALETTED = new int[][]{
		{0, 0, 0},
		{17, 17, 17},
		{34, 34, 34},
		{51, 51, 51},
		{68, 68, 68},
		{85, 85, 85},
		{102, 102, 102},
		{119, 119, 119},
		{136, 136, 136},
		{153, 153, 153},
		{170, 170, 170},
		{187, 187, 187},
		{204, 204, 204},
		{221, 221, 221},
		{238, 238, 238},
		{255, 255, 255},
		{64, 33, 0},
		{85, 44, 0},
		{106, 55, 0},
		{128, 66, 0},
		{149, 77, 0},
		{170, 88, 0},
		{191, 99, 0},
		{213, 110, 0},
		{234, 121, 0},
		{255, 132, 0},
		{255, 158, 55},
		{255, 167, 73},
		{255, 176, 91},
		{255, 184, 109},
		{255, 193, 128},
		{255, 202, 146},
		{64, 16, 0},
		{85, 22, 0},
		{106, 28, 0},
		{128, 33, 0},
		{149, 39, 0},
		{170, 44, 0},
		{191, 49, 0},
		{213, 55, 0},
		{234, 60, 0},
		{255, 66, 0},
		{255, 107, 55},
		{255, 120, 73},
		{255, 134, 91},
		{255, 147, 109},
		{255, 161, 128},
		{255, 174, 146},
		{64, 0, 10},
		{85, 0, 13},
		{106, 0, 17},
		{128, 0, 20},
		{149, 0, 23},
		{170, 0, 27},
		{191, 0, 30},
		{213, 0, 33},
		{234, 0, 37},
		{255, 0, 40},
		{255, 55, 86},
		{255, 73, 101},
		{255, 91, 117},
		{255, 109, 132},
		{255, 128, 148},
		{255, 146, 163},
		{64, 0, 36},
		{85, 0, 48},
		{106, 0, 60},
		{128, 0, 72},
		{149, 0, 83},
		{170, 0, 95},
		{191, 0, 107},
		{213, 0, 119},
		{234, 0, 131},
		{255, 0, 143},
		{255, 55, 167},
		{255, 73, 175},
		{255, 91, 183},
		{255, 109, 191},
		{255, 128, 199},
		{255, 146, 207},
		{63, 0, 64},
		{84, 0, 85},
		{105, 0, 106},
		{127, 0, 128},
		{148, 0, 149},
		{169, 0, 170},
		{190, 0, 191},
		{211, 0, 213},
		{232, 0, 234},
		{253, 0, 255},
		{253, 55, 255},
		{254, 73, 255},
		{254, 91, 255},
		{254, 109, 255},
		{254, 128, 255},
		{254, 146, 255},
		{38, 0, 64},
		{51, 0, 85},
		{64, 0, 106},
		{77, 0, 128},
		{90, 0, 149},
		{103, 0, 170},
		{115, 0, 191},
		{128, 0, 213},
		{141, 0, 234},
		{154, 0, 255},
		{176, 55, 255},
		{183, 73, 255},
		{190, 91, 255},
		{197, 109, 255},
		{204, 128, 255},
		{212, 146, 255},
		{10, 0, 64},
		{14, 0, 85},
		{17, 0, 106},
		{20, 0, 128},
		{24, 0, 149},
		{27, 0, 170},
		{31, 0, 191},
		{34, 0, 213},
		{38, 0, 234},
		{41, 0, 255},
		{87, 55, 255},
		{102, 73, 255},
		{117, 91, 255},
		{133, 109, 255},
		{148, 128, 255},
		{163, 146, 255},
		{0, 24, 64},
		{0, 31, 85},
		{0, 39, 106},
		{0, 47, 128},
		{0, 55, 149},
		{0, 63, 170},
		{0, 71, 191},
		{0, 78, 213},
		{0, 86, 234},
		{0, 94, 255},
		{55, 129, 255},
		{73, 140, 255},
		{91, 152, 255},
		{109, 163, 255},
		{128, 175, 255},
		{146, 186, 255},
		{0, 47, 64},
		{0, 63, 85},
		{0, 79, 106},
		{0, 95, 128},
		{0, 110, 149},
		{0, 126, 170},
		{0, 142, 191},
		{0, 158, 213},
		{0, 173, 234},
		{0, 189, 255},
		{55, 203, 255},
		{73, 208, 255},
		{91, 213, 255},
		{109, 217, 255},
		{128, 222, 255},
		{146, 227, 255},
		{0, 64, 41},
		{0, 85, 54},
		{0, 106, 68},
		{0, 128, 81},
		{0, 149, 95},
		{0, 170, 108},
		{0, 191, 122},
		{0, 213, 135},
		{0, 234, 149},
		{0, 255, 162},
		{55, 255, 182},
		{73, 255, 189},
		{91, 255, 195},
		{109, 255, 202},
		{128, 255, 209},
		{146, 255, 215},
		{0, 64, 16},
		{0, 85, 21},
		{0, 106, 27},
		{0, 128, 32},
		{0, 149, 37},
		{0, 170, 43},
		{0, 191, 48},
		{0, 213, 53},
		{0, 234, 59},
		{0, 255, 64},
		{55, 255, 105},
		{73, 255, 119},
		{91, 255, 132},
		{109, 255, 146},
		{128, 255, 160},
		{146, 255, 173},
		{22, 64, 0},
		{30, 85, 0},
		{37, 106, 0},
		{45, 128, 0},
		{52, 149, 0},
		{60, 170, 0},
		{67, 191, 0},
		{75, 213, 0},
		{82, 234, 0},
		{90, 255, 0},
		{125, 255, 55},
		{137, 255, 73},
		{149, 255, 91},
		{161, 255, 109},
		{172, 255, 128},
		{184, 255, 146},
		{49, 64, 0},
		{65, 85, 0},
		{82, 106, 0},
		{98, 128, 0},
		{114, 149, 0},
		{131, 170, 0},
		{147, 191, 0},
		{163, 213, 0},
		{180, 234, 0},
		{196, 255, 0},
		{209, 255, 55},
		{213, 255, 73},
		{217, 255, 91},
		{221, 255, 109},
		{226, 255, 128},
		{230, 255, 146},
		{58, 64, 0},
		{77, 85, 0},
		{97, 106, 0},
		{116, 128, 0},
		{135, 149, 0},
		{155, 170, 0},
		{174, 191, 0},
		{193, 213, 0},
		{213, 234, 0},
		{232, 255, 0},
		{237, 255, 55},
		{239, 255, 73},
		{240, 255, 91},
		{242, 255, 109},
		{244, 255, 128},
		{245, 255, 146},
		{64, 45, 0},
		{85, 60, 0},
		{106, 75, 0},
		{128, 90, 0},
		{149, 104, 0},
		{170, 119, 0},
		{191, 134, 0},
		{213, 149, 0},
		{234, 164, 0},
		{255, 179, 0},
		{255, 195, 55},
		{255, 201, 73},
		{255, 206, 91},
		{255, 212, 109},
		{255, 217, 128},
		{255, 222, 146},
		{0, 0, 0},
		{17, 17, 17},
		{34, 34, 34},
		{51, 51, 51},
		{68, 68, 68},
		{85, 85, 85},
		{102, 102, 102},
		{119, 119, 119},
		{136, 136, 136},
		{153, 153, 153},
		{170, 170, 170},
		{187, 187, 187},
		{204, 204, 204},
		{221, 221, 221},
		{238, 238, 238},
		{255, 255, 255},
		{64, 33, 0},
		{85, 44, 0},
		{106, 55, 0},
		{128, 66, 0},
		{149, 77, 0},
		{170, 88, 0},
		{191, 99, 0},
		{213, 110, 0},
		{234, 121, 0},
		{255, 132, 0},
		{255, 158, 55},
		{255, 167, 73},
		{255, 176, 91},
		{255, 184, 109},
		{255, 193, 128},
		{255, 202, 146},
		{64, 16, 0},
		{85, 22, 0},
		{106, 28, 0},
		{128, 33, 0},
		{149, 39, 0},
		{170, 44, 0},
		{191, 49, 0},
		{213, 55, 0},
		{234, 60, 0},
		{255, 66, 0},
		{255, 107, 55},
		{255, 120, 73},
		{255, 134, 91},
		{255, 147, 109},
		{255, 161, 128},
		{255, 174, 146},
		{64, 0, 10},
		{85, 0, 13},
		{106, 0, 17},
		{128, 0, 20},
		{149, 0, 23},
		{170, 0, 27},
		{191, 0, 30},
		{213, 0, 33},
		{234, 0, 37},
		{255, 0, 40},
		{255, 55, 86},
		{255, 73, 101},
		{255, 91, 117},
		{255, 109, 132},
		{255, 128, 148},
		{255, 146, 163},
		{64, 0, 36},
		{85, 0, 48},
		{106, 0, 60},
		{128, 0, 72},
		{149, 0, 83},
		{170, 0, 95},
		{191, 0, 107},
		{213, 0, 119},
		{234, 0, 131},
		{255, 0, 143},
		{255, 55, 167},
		{255, 73, 175},
		{255, 91, 183},
		{255, 109, 191},
		{255, 128, 199},
		{255, 146, 207},
		{63, 0, 64},
		{84, 0, 85},
		{105, 0, 106},
		{127, 0, 128},
		{148, 0, 149},
		{169, 0, 170},
		{190, 0, 191},
		{211, 0, 213},
		{232, 0, 234},
		{253, 0, 255},
		{253, 55, 255},
		{254, 73, 255},
		{254, 91, 255},
		{254, 109, 255},
		{254, 128, 255},
		{254, 146, 255},
		{38, 0, 64},
		{51, 0, 85},
		{64, 0, 106},
		{77, 0, 128},
		{90, 0, 149},
		{103, 0, 170},
		{115, 0, 191},
		{128, 0, 213},
		{141, 0, 234},
		{154, 0, 255},
		{176, 55, 255},
		{183, 73, 255},
		{190, 91, 255},
		{197, 109, 255},
		{204, 128, 255},
		{212, 146, 255},
		{10, 0, 64},
		{14, 0, 85},
		{17, 0, 106},
		{20, 0, 128},
		{24, 0, 149},
		{27, 0, 170},
		{31, 0, 191},
		{34, 0, 213},
		{38, 0, 234},
		{41, 0, 255},
		{87, 55, 255},
		{102, 73, 255},
		{117, 91, 255},
		{133, 109, 255},
		{148, 128, 255},
		{163, 146, 255},
		{0, 24, 64},
		{0, 31, 85},
		{0, 39, 106},
		{0, 47, 128},
		{0, 55, 149},
		{0, 63, 170},
		{0, 71, 191},
		{0, 78, 213},
		{0, 86, 234},
		{0, 94, 255},
		{55, 129, 255},
		{73, 140, 255},
		{91, 152, 255},
		{109, 163, 255},
		{128, 175, 255},
		{146, 186, 255},
		{0, 47, 64},
		{0, 63, 85},
		{0, 79, 106},
		{0, 95, 128},
		{0, 110, 149},
		{0, 126, 170},
		{0, 142, 191},
		{0, 158, 213},
		{0, 173, 234},
		{0, 189, 255},
		{55, 203, 255},
		{73, 208, 255},
		{91, 213, 255},
		{109, 217, 255},
		{128, 222, 255},
		{146, 227, 255},
		{0, 64, 41},
		{0, 85, 54},
		{0, 106, 68},
		{0, 128, 81},
		{0, 149, 95},
		{0, 170, 108},
		{0, 191, 122},
		{0, 213, 135},
		{0, 234, 149},
		{0, 255, 162},
		{55, 255, 182},
		{73, 255, 189},
		{91, 255, 195},
		{109, 255, 202},
		{128, 255, 209},
		{146, 255, 215},
		{0, 64, 16},
		{0, 85, 21},
		{0, 106, 27},
		{0, 128, 32},
		{0, 149, 37},
		{0, 170, 43},
		{0, 191, 48},
		{0, 213, 53},
		{0, 234, 59},
		{0, 255, 64},
		{55, 255, 105},
		{73, 255, 119},
		{91, 255, 132},
		{109, 255, 146},
		{128, 255, 160},
		{146, 255, 173},
		{22, 64, 0},
		{30, 85, 0},
		{37, 106, 0},
		{45, 128, 0},
		{52, 149, 0},
		{60, 170, 0},
		{67, 191, 0},
		{75, 213, 0},
		{82, 234, 0},
		{90, 255, 0},
		{125, 255, 55},
		{137, 255, 73},
		{149, 255, 91},
		{161, 255, 109},
		{172, 255, 128},
		{184, 255, 146},
		{49, 64, 0},
		{65, 85, 0},
		{82, 106, 0},
		{98, 128, 0},
		{114, 149, 0},
		{131, 170, 0},
		{147, 191, 0},
		{163, 213, 0},
		{180, 234, 0},
		{196, 255, 0},
		{209, 255, 55},
		{213, 255, 73},
		{217, 255, 91},
		{221, 255, 109},
		{226, 255, 128},
		{230, 255, 146},
		{58, 64, 0},
		{77, 85, 0},
		{97, 106, 0},
		{116, 128, 0},
		{135, 149, 0},
		{155, 170, 0},
		{174, 191, 0},
		{193, 213, 0},
		{213, 234, 0},
		{232, 255, 0},
		{237, 255, 55},
		{239, 255, 73},
		{240, 255, 91},
		{242, 255, 109},
		{244, 255, 128},
		{245, 255, 146},
		{64, 45, 0},
		{85, 60, 0},
		{106, 75, 0},
		{128, 90, 0},
		{149, 104, 0},
		{170, 119, 0},
		{191, 134, 0},
		{213, 149, 0},
		{234, 164, 0},
		{255, 179, 0},
		{255, 195, 55},
		{255, 201, 73},
		{255, 206, 91},
		{255, 212, 109},
		{255, 217, 128},
		{255, 222, 146}
	};
	public static final int[][] COLOR_PALETTED2 = new int[][] { { 0, 0, 0 },
			{ 37, 37, 37 }, { 52, 52, 52 }, { 78, 78, 78 }, { 104, 104, 104 },
			{ 117, 117, 117 }, { 142, 142, 142 }, { 164, 164, 164 },
			{ 184, 184, 184 }, { 197, 197, 197 }, { 208, 208, 208 },
			{ 215, 215, 215 }, { 225, 225, 225 }, { 234, 234, 234 },
			{ 244, 244, 244 }, { 255, 255, 255 }, { 65, 32, 0 }, { 84, 40, 0 },
			{ 118, 55, 0 }, { 154, 80, 0 }, { 195, 104, 6 }, { 228, 123, 7 },
			{ 255, 145, 26 }, { 255, 171, 29 }, { 255, 197, 31 },
			{ 255, 208, 59 }, { 255, 216, 76 }, { 255, 230, 81 },
			{ 255, 244, 86 }, { 255, 249, 112 }, { 255, 255, 144 },
			{ 255, 255, 170 }, { 69, 25, 4 }, { 114, 30, 17 }, { 159, 36, 30 },
			{ 179, 58, 32 }, { 200, 81, 32 }, { 227, 105, 32 },
			{ 252, 129, 32 }, { 253, 140, 37 }, { 254, 152, 44 },
			{ 255, 174, 56 }, { 255, 185, 70 }, { 255, 191, 81 },
			{ 255, 198, 109 }, { 255, 213, 135 }, { 255, 228, 152 },
			{ 255, 230, 171 }, { 93, 31, 12 }, { 122, 36, 13 },
			{ 152, 44, 14 }, { 176, 47, 15 }, { 191, 54, 36 }, { 211, 78, 42 },
			{ 231, 98, 62 }, { 243, 110, 74 }, { 253, 120, 84 },
			{ 255, 138, 106 }, { 255, 152, 124 }, { 255, 164, 139 },
			{ 255, 179, 158 }, { 255, 194, 178 }, { 255, 208, 195 },
			{ 255, 218, 208 }, { 74, 23, 0 }, { 114, 31, 0 }, { 168, 19, 0 },
			{ 200, 33, 10 }, { 223, 37, 18 }, { 236, 59, 36 }, { 250, 82, 54 },
			{ 252, 97, 72 }, { 255, 112, 95 }, { 255, 126, 126 },
			{ 255, 143, 143 }, { 255, 157, 158 }, { 255, 171, 173 },
			{ 255, 185, 189 }, { 255, 199, 206 }, { 255, 202, 222 },
			{ 73, 0, 54 }, { 102, 0, 75 }, { 128, 3, 95 }, { 149, 15, 116 },
			{ 170, 34, 136 }, { 186, 61, 153 }, { 202, 77, 169 },
			{ 215, 90, 182 }, { 228, 103, 195 }, { 239, 114, 206 },
			{ 251, 126, 218 }, { 255, 141, 225 }, { 255, 157, 229 },
			{ 255, 165, 231 }, { 255, 175, 234 }, { 255, 184, 236 },
			{ 72, 3, 108 }, { 92, 4, 136 }, { 101, 13, 144 }, { 123, 35, 167 },
			{ 147, 59, 191 }, { 157, 69, 201 }, { 167, 79, 211 },
			{ 178, 90, 222 }, { 189, 101, 233 }, { 197, 109, 241 },
			{ 206, 118, 250 }, { 213, 131, 255 }, { 218, 144, 255 },
			{ 222, 156, 255 }, { 226, 169, 255 }, { 230, 182, 255 },
			{ 5, 30, 129 }, { 6, 38, 165 }, { 8, 47, 202 }, { 38, 61, 212 },
			{ 68, 76, 222 }, { 79, 90, 236 }, { 90, 104, 255 },
			{ 101, 117, 255 }, { 113, 131, 255 }, { 128, 145, 255 },
			{ 144, 160, 255 }, { 151, 169, 255 }, { 159, 178, 255 },
			{ 175, 190, 255 }, { 192, 203, 255 }, { 205, 211, 255 },
			{ 11, 7, 121 }, { 32, 28, 142 }, { 53, 49, 163 }, { 70, 66, 180 },
			{ 87, 83, 197 }, { 97, 93, 207 }, { 109, 105, 219 },
			{ 123, 119, 233 }, { 137, 133, 247 }, { 145, 141, 255 },
			{ 156, 152, 255 }, { 167, 164, 255 }, { 178, 175, 255 },
			{ 187, 184, 255 }, { 195, 193, 255 }, { 211, 209, 255 },
			{ 29, 41, 90 }, { 29, 56, 118 }, { 29, 72, 146 }, { 29, 92, 172 },
			{ 29, 113, 198 }, { 50, 134, 207 }, { 72, 155, 217 },
			{ 78, 168, 236 }, { 85, 182, 255 }, { 105, 202, 255 },
			{ 116, 203, 255 }, { 130, 211, 255 }, { 141, 218, 255 },
			{ 159, 212, 255 }, { 180, 226, 255 }, { 192, 235, 255 },
			{ 0, 75, 89 }, { 0, 93, 110 }, { 0, 111, 132 }, { 0, 132, 156 },
			{ 0, 153, 191 }, { 0, 171, 202 }, { 0, 188, 222 }, { 0, 208, 245 },
			{ 16, 220, 255 }, { 62, 225, 255 }, { 100, 231, 255 },
			{ 118, 234, 255 }, { 139, 237, 255 }, { 154, 239, 255 },
			{ 177, 243, 255 }, { 199, 246, 255 }, { 0, 72, 0 }, { 0, 84, 0 },
			{ 3, 107, 3 }, { 14, 118, 14 }, { 24, 128, 24 }, { 39, 146, 39 },
			{ 54, 164, 54 }, { 78, 185, 78 }, { 81, 205, 81 },
			{ 114, 218, 114 }, { 124, 228, 124 }, { 133, 237, 133 },
			{ 153, 242, 153 }, { 179, 247, 179 }, { 195, 249, 195 },
			{ 205, 252, 205 }, { 22, 64, 0 }, { 28, 83, 0 }, { 35, 102, 0 },
			{ 40, 120, 0 }, { 46, 140, 0 }, { 58, 152, 12 }, { 71, 165, 25 },
			{ 81, 175, 35 }, { 92, 186, 46 }, { 113, 207, 67 },
			{ 133, 227, 87 }, { 141, 235, 95 }, { 151, 245, 105 },
			{ 160, 254, 114 }, { 177, 255, 138 }, { 188, 255, 154 },
			{ 44, 53, 0 }, { 56, 68, 0 }, { 68, 82, 0 }, { 73, 86, 0 },
			{ 96, 113, 0 }, { 108, 127, 0 }, { 121, 141, 10 },
			{ 139, 159, 28 }, { 158, 178, 47 }, { 171, 191, 60 },
			{ 184, 204, 73 }, { 194, 214, 83 }, { 205, 225, 83 },
			{ 219, 239, 108 }, { 232, 252, 121 }, { 242, 255, 171 },
			{ 70, 58, 9 }, { 77, 63, 9 }, { 84, 69, 9 }, { 108, 88, 9 },
			{ 144, 118, 9 }, { 171, 139, 10 }, { 193, 161, 32 },
			{ 208, 176, 47 }, { 222, 190, 61 }, { 230, 198, 69 },
			{ 237, 205, 76 }, { 245, 216, 98 }, { 251, 226, 118 },
			{ 252, 238, 152 }, { 253, 243, 169 }, { 253, 243, 190 },
			{ 64, 26, 2 }, { 88, 31, 5 }, { 112, 36, 8 }, { 141, 58, 19 },
			{ 171, 81, 31 }, { 181, 100, 39 }, { 191, 119, 48 },
			{ 191, 119, 48 }, { 225, 147, 68 }, { 237, 160, 78 },
			{ 249, 173, 88 }, { 252, 183, 92 }, { 255, 193, 96 },
			{ 255, 202, 105 }, { 255, 209, 128 }, { 255, 218, 150 }, };

}
