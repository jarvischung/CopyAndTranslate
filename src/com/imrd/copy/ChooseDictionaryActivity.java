package com.imrd.copy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioButton;

public class ChooseDictionaryActivity extends Activity {

	private static int radioAry[] = {R.id.dict_1,R.id.dict_2,R.id.dict_3,R.id.dict_4};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_dict);
		
		RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.myRadioGroup);
		myRadioGroup.setOnCheckedChangeListener(chooseDictionary);
		
		((RadioButton)findViewById(radioAry[CopyAndTranslateActivity.dictionaryIndex])).setChecked(true);
		
	}

	private RadioGroup.OnCheckedChangeListener chooseDictionary = new RadioGroup.OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.dict_1:
				CopyAndTranslateActivity.dictionaryIndex = 0;
				break;
			case R.id.dict_2:
				CopyAndTranslateActivity.dictionaryIndex = 1;
				break;
			case R.id.dict_3:
				CopyAndTranslateActivity.dictionaryIndex = 2;
				break;
			case R.id.dict_4:
				CopyAndTranslateActivity.dictionaryIndex = 3;
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
