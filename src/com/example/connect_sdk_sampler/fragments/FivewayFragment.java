package com.example.connect_sdk_sampler.fragments;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.connectsdk.core.KeyboardInputInfo;
import com.connectsdk.core.KeyboardInputInfo.KeyboardType;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.webos.WebOSTVKeyboardInput.KeyboardInputListener;
import com.example.connect_sdk_sampler.R;

public class FivewayFragment extends BaseFragment {
    public Button upButton;
    public Button leftButton;
    public Button clickButton;
    public Button rightButton;
    public Button backButton;
    public Button downButton;
    public Button homeButton;
    public View trackpadView;

    boolean isDown = false;
    boolean isMoving = false;
    boolean isScroll = false;
    
    float startX;
    float startY;

    float lastX = Float.NaN;
    float lastY = Float.NaN;
    
    int scrollDx, scrollDy;
    long eventStart = 0;
    Timer timer = new Timer();
    TimerTask autoScrollTimerTask;

    boolean canReplaceText = false;

    EditText editText;
    TextWatcher filterTextWatcher;

    public FivewayFragment(ConnectableDevice tv, Context context)
    {
        super(tv, context);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_fiveway, container, false);

        upButton = (Button) rootView.findViewById(R.id.upButton);
        leftButton = (Button) rootView.findViewById(R.id.leftButton);
        clickButton = (Button) rootView.findViewById(R.id.clickButton);
        rightButton = (Button) rootView.findViewById(R.id.rightButton);
        backButton = (Button) rootView.findViewById(R.id.backButton);
        downButton = (Button) rootView.findViewById(R.id.downButton);
        homeButton = (Button) rootView.findViewById(R.id.homeButton);
        trackpadView = rootView.findViewById(R.id.trackpadView);

        buttons = new Button[7];
        buttons[0] = upButton;
        buttons[1] = leftButton;
        buttons[2] = clickButton;
        buttons[3] = rightButton;
        buttons[4] = backButton;
        buttons[5] = downButton;
        buttons[6] = homeButton;
        
        editText = (EditText) rootView.findViewById(R.id.editField);
        
        clearLocalText();
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        editText.addTextChangedListener(new TextWatcher () {
        	String lastString = "";
        	
			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.length() == 0) {
					// Nasty hack to give Android some deleteable charcters (zero-width space)
					editable.append("\u200B");
				}
				
				lastString = editable.toString().replace("\u200B", "");
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int beforeLength, int charsChanged) {
				if ( getKeyboardControl() == null ) {
					System.err.println("Keyboard Control is null");
					return;
				}
				
				System.out.println("[DEBUG] appside: " + s);
				System.out.println("[DEBUG] len: " + s.length());
				System.out.println("[DEBUG] lastString: " + lastString);

				if (s.length() == 0 ) { 
					// all characters including the sentinel were deleted
					getKeyboardControl().sendDelete();
					
				} else {
					String newString = s.toString().replace("\u200B", ""); // nasty hack
					System.out.println("[DEBUG] newString: " + newString);
					int matching = getMatchingCharacterLength(lastString, newString);
					
					if (matching == 0) {
						getKeyboardControl().send("");
					} else if (matching < lastString.length()) {
						// Delete old characters
						for (int i = 0; i < lastString.length() - matching; i++) {
							getKeyboardControl().sendDelete();
						}
					}
					
					if (matching < newString.length()) {
						// Insert new characters
						getKeyboardControl().send(newString.substring(matching));
					}
				}
			}
        });
        
        editText.setOnEditorActionListener(new OnEditorActionListener () {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				getKeyboardControl().sendEnter();
				return false;
			}
        });
        
        
        editText.setOnKeyListener(new OnKeyListener() {                 
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
 				if(keyCode == KeyEvent.KEYCODE_DEL){  
 					getKeyboardControl().sendDelete();
                 }
                 return false;       
            }
        });
        
		return rootView;
	}
	
    void clearLocalText() {
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
		    	editText.setText("\u200B");
			}
		});
    }

    int getMatchingCharacterLength (String oldString, String newString) {
    	char [] oldChars = oldString.toCharArray();
    	char [] newChars = newString.toCharArray();
    	
    	int length = Math.min(oldChars.length, newChars.length);
    	
    	for (int i = 0; i < length; i++) {
    		if (oldChars[i] != newChars[i]) {
    			return i;
    		}
    	}
    	
    	return length;
    }

    @Override
    public void disableButtons()
    {
        trackpadView.setOnTouchListener(null);

        super.disableButtons();
    }

    @Override
    public void enableButtons()
    {
    	if ( getMouseControl() != null ) {
        	getMouseControl().connectMouse();
    	}
    	
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().up(null);
            	}
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().left(null);
            	}
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().ok(null);
            	}
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().right(null);
            	}
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().back(null);
            	}
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().down(null);
            	}
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( getFivewayControl() != null ) {
            		getFivewayControl().home(null);
            	}
            }
        });
        
        if ( getKeyboardControl() != null ) {
        	getKeyboardControl().subscribeKeyboard(new KeyboardInputListener() {
				
				@Override
				public void onGetKeyboardInputSuccess(KeyboardInputInfo keyboard) {
					boolean focused = keyboard.isFocused();
					KeyboardType keyboardType = keyboard.getKeyboardType();
					boolean predictionEnabled = keyboard.isPredictionEnabled();
					boolean correctionEnabled = keyboard.isCorrectionEnabled();
					boolean autoCapitalization = keyboard.isAutoCapitalization();
					boolean hiddenText = keyboard.isHiddenText();
					boolean focusChanged = keyboard.isFocusChanged();
					int type; 
					
					if (keyboardType != KeyboardType.DEFAULT) {
						if (keyboardType == KeyboardType.NUMBER) {
							type = InputType.TYPE_CLASS_NUMBER;
						} 
						else if (keyboardType == KeyboardType.PHONE_NUMBER ) {
							type = InputType.TYPE_CLASS_PHONE;
						} 
						else {
							type = InputType.TYPE_CLASS_TEXT;
							
							if (keyboardType == KeyboardType.URL ) {
								type |= InputType.TYPE_TEXT_VARIATION_URI;
							} else if (keyboardType == KeyboardType.EMAIL) {
								type |= InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
							}
							
							if ( predictionEnabled ) {
								type |= InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
							}
							
							if ( correctionEnabled ) {
								type |= InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
							}
							
							if ( autoCapitalization ) {
								type |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
							}
							
							if ( hiddenText ) {
								type |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
							}
							
							if (!canReplaceText) {
								type |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
							}
						}
	
						final int fType = type;
						if (editText.getInputType() != type) {
							runOnUiThread(new Runnable() {
								public void run() {
									editText.setInputType(fType);
								}
							});
						}
					}
					
					if (focused) {
						if ( focusChanged ) {
							clearLocalText();
						}
						runOnUiThread(new Runnable() {
							public void run() {
								editText.requestFocus();
								InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
								mgr.showSoftInput(((Activity)mContext).getCurrentFocus(), InputMethodManager.SHOW_FORCED);
							}
						});
					} else {
						canReplaceText = false;
						runOnUiThread(new Runnable() {
							public void run() {
								InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
								mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
							}
						});
						clearLocalText();
					}					
				}
				
				@Override
				public void onGetKeyboardInputFailed(ServiceCommandError arg0) {
				}
			});
        }
        
        trackpadView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float dx = 0, dy = 0;
                
                boolean wasMoving = isMoving;
                boolean wasScroll = isScroll;
                
                isScroll = isScroll || motionEvent.getPointerCount() > 1;
                
                switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    isDown = true;
                    eventStart = motionEvent.getEventTime();
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    isDown = false;
                    isMoving = false;
                    isScroll = false;
                    lastX = Float.NaN;
                    lastY = Float.NaN;
                    break;
                }
                
                if (lastX != Float.NaN || lastY != Float.NaN) {
                    dx = Math.round(motionEvent.getX() - lastX);
                    dy = Math.round(motionEvent.getY() - lastY);
                }
                
                lastX = motionEvent.getX();
                lastY = motionEvent.getY();
                
                float xDistFromStart = Math.abs(motionEvent.getX() - startX);
                float yDistFromStart = Math.abs(motionEvent.getY() - startY);
                
                if (isDown && !isMoving) {
                    if (xDistFromStart > 10 && yDistFromStart > 10) {
                        isMoving = true;
                    }
                }
                
                if (isDown && isMoving) {
                    if (dx != 0 && dy != 0) {
                        // Scale dx and dy to simulate acceleration
                        int dxSign = dx >= 0 ? 1 : -1;
                        int dySign = dy >= 0 ? 1 : -1;
                        
                        dx = dxSign * Math.round(Math.pow(Math.abs(dx), 1.1));
                        dy = dySign * Math.round(Math.pow(Math.abs(dy), 1.1));
                        
                        if (!isScroll) {
                        	if ( getMouseControl() != null ) 
                        		getMouseControl().move(dx, dy);
                        } else {
                            long now = SystemClock.uptimeMillis();
                            
                            scrollDx = (int)(motionEvent.getX() - startX);
                            scrollDy = (int)(motionEvent.getY() - startY);
                            
                            if (now - eventStart > 1000 && autoScrollTimerTask == null) {
                                Log.d("main", "starting autoscroll");
                                // start autoscrolling
                                autoScrollTimerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                    	if ( getMouseControl() != null )
                                    		getMouseControl().scroll(scrollDx, scrollDy);
                                    }
                                };
                                
                                timer.schedule(autoScrollTimerTask, 100, 750);
                            }
                        }
                    }
                } else if (!isDown && !wasMoving) {
                	if ( getMouseControl() != null ) 
                		getMouseControl().click();
                } else if (!isDown && wasMoving && wasScroll) {
                    // release two fingers
                    dx = motionEvent.getX() - startX;
                    dy = motionEvent.getY() - startY;
                    
                    if ( getMouseControl() != null )
                    	getMouseControl().scroll(dx, dy);
                    Log.d("main", "sending scroll " + dx + " ," + dx);
                }
                
                if (!isDown) {
                    isMoving = false;
                    
                    if (autoScrollTimerTask != null) {
                        autoScrollTimerTask.cancel();
                        autoScrollTimerTask = null;
                        
                        Log.d("main", "ending autoscroll");
                    }
                }

                return true;
            }
        });

        super.enableButtons();
    }
}
