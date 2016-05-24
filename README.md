# Double seek bar
![double_seek_bar][1]


  [1]: https://github.com/AlphaBoom/DoubleSeekbar/raw/master/ScreenShots/double_seekbar.gif
  
#Usage
 Add a dependency to your `build.gradle`:
 
 ```
 compile 'wzd.anarchy:library:0.2.0'
 ```

     <com.anarchy.library.DoubleSeekBar
       android:padding_left="2dp"
       android:padding_right="2dp"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"/>
  Set callback 

     DoubleSeekBar seekBar = (DoubleSeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new DoubleSeekBar.onSeekBarChangeListener() {
            @Override
            public void onProgressChanged(DoubleSeekBar doubleSeekBar, float firstThumbRatio, float secondThumbRatio) {
                
            }
        });
        
  Reset with animation
     
     DoubleSeekBar seekBar = (DoubleSeekBar) findViewById(R.id.seek_bar);
     seekBar.reset();
        
#Custom

     /**
     * override this method
     * custom own tip text
     * @param ratio firstThumb and secondThumb range 0.0-1.0;
     * @return
     */
    protected String ratio2DateString(float ratio) 
    
    
  modify style
  
    <declare-styleable name="DoubleSeekBar">
        <attr name="DB_ThumbRadius" format="dimension"/>
        <attr name="DB_ThumbColor" format="color"/>
        <attr name="DB_ProgressColor" format="color"/>
        <attr name="DB_ProgressBackgroundColor" format="color"/>
        <attr name="DB_ProgressWidth" format="dimension"/>
        <attr name="DB_TextSize" format="dimension"/>
        <attr name="DB_TextColor" format="color"/>
    </declare-styleable>
