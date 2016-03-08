# Double seek bar
![double_seek_bar][1]


  [1]: https://github.com/AlphaBoom/DoubleSeekbar/raw/master/ScreenShots/double_seekbar.gif
  
#Usage
 Add a dependency to your `build.gradle`:
 
 ```
 compile 'wzd.anarchy:library:unspecified'
 ```

     <com.anarchy.library.DoubleSeekBar
       android:layout_width="match_parent"
       android:layout_height="wrap_content"/>
#Set callback 

     DoubleSeekBar seekBar = (DoubleSeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new DoubleSeekBar.onSeekBarChangeListener() {
            @Override
            public void onProgressChanged(DoubleSeekBar doubleSeekBar, float firstThumbRatio, float secondThumbRatio) {
                
            }
        });
        
#Custom

     /**
     * override this method
     * custom own tip text
     * @param ratio firstThumb and secondThumb range 0.0-1.0;
     * @return
     */
    protected String ratio2DateString(float ratio) {
