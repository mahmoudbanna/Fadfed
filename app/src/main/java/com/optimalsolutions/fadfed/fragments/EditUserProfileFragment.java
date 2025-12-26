package com.optimalsolutions.fadfed.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.optimalsolutions.fadfed.AppController;
import com.optimalsolutions.fadfed.R;
import com.optimalsolutions.fadfed.network.ErrorHandler;
import com.optimalsolutions.fadfed.network.StringResponseHandler;
import com.optimalsolutions.fadfed.utils.Alerts;
import com.optimalsolutions.fadfed.network.NetworkHandler;
import com.optimalsolutions.fadfed.utils.PicturesUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditUserProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private View v;
    private Spinner countrysp;
    private Button usernamesavebut, useremailsavebut, userpwsavebut, saveuserinfobut;
    private EditText usernameet, useremailet, userpwet;
    private RadioGroup agerg, sexrg, countryrg, sexvaluerg;
    private ImageButton adduserpicbut;
    private NetworkImageView userpicimg;
    private TextView agesp;
    private String selectedImagePath , password;

    private RadioButton hideagecb, showagecb, hidesexcb, showsexcb, hidecountrycb, showcountrycb;
    private static final int SELECT_PICTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_edit_user_profile, null);


        usernamesavebut = (Button) v.findViewById(R.id.usernamesavebut);
        userpwsavebut = (Button) v.findViewById(R.id.userpwsavebut);
        useremailsavebut = (Button) v.findViewById(R.id.useremailsavebut);
        saveuserinfobut = (Button) v.findViewById(R.id.saveuserinfobut);

        hideagecb = (RadioButton) v.findViewById(R.id.hideagecb);
        showagecb = (RadioButton) v.findViewById(R.id.showagecb);
        hidesexcb = (RadioButton) v.findViewById(R.id.hidesexcb);
        showsexcb = (RadioButton) v.findViewById(R.id.showsexcb);
        hidecountrycb = (RadioButton) v.findViewById(R.id.hidecountrycb);
        showcountrycb = (RadioButton) v.findViewById(R.id.showcountrycb);

        usernameet = (EditText) v.findViewById(R.id.usernameet);
        useremailet = (EditText) v.findViewById(R.id.useremailet);
        userpwet = (EditText) v.findViewById(R.id.userpwet);
        usernameet.setText(AppController.getInstance().getNickName());
        useremailet.setText(AppController.getInstance().getEmail());
        userpwet.setText(AppController.getInstance().getPassword());


        agesp = (TextView) v.findViewById(R.id.agesp);
        countrysp = (Spinner) v.findViewById(R.id.countrysp);

        agerg = (RadioGroup) v.findViewById(R.id.agerg);
        sexrg = (RadioGroup) v.findViewById(R.id.sexrg);
        countryrg = (RadioGroup) v.findViewById(R.id.countryrg);
        sexvaluerg = (RadioGroup) v.findViewById(R.id.sexvaluerg);

        adduserpicbut = (ImageButton) v.findViewById(R.id.adduserpicbut);

        userpicimg = (NetworkImageView) v.findViewById(R.id.userpicimg);

        countrysp.setAdapter(new ArrayAdapter(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.arab_countries)));

        agesp.setText(AppController.getInstance().getBirthDate());

        switch (Integer.parseInt(AppController.getInstance().getGender())) {
            case 1:
                sexvaluerg.check(R.id.malerb);
                break;
            case 2:
                sexvaluerg.check(R.id.femalerb);
                break;
            default:
                break;
        }
        countrysp.setSelection(getCountryNameArrayIndex(AppController.getInstance().getCountry()));

        if (AppController.getInstance().getShowBirthDate().equalsIgnoreCase("1"))
            showagecb.setChecked(true);
        else
            hideagecb.setChecked(true);

        if (AppController.getInstance().getShowGender().equalsIgnoreCase("1"))
            showsexcb.setChecked(true);
        else
            hidesexcb.setChecked(true);

        if (AppController.getInstance().getShowCountry().equalsIgnoreCase("1"))
            showcountrycb.setChecked(true);
        else
            hidecountrycb.setChecked(true);


        useremailsavebut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveNewUserEmail();

            }
        });
        usernamesavebut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                saveNewUserName();

            }
        });
        userpwsavebut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveNewUserPassword();
            }
        });
        saveuserinfobut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveNewUserInfo();
            }
        });
        adduserpicbut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        SELECT_PICTURE);

            }
        });

        userpicimg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        SELECT_PICTURE);

            }
        });

        agesp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Alerts.showDatePickerDialog(EditUserProfileFragment.this);
                }
                return true;
            }
        });
        agesp.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    Alerts.showDatePickerDialog(EditUserProfileFragment.this);
                    return true;
                } else {
                    return false;
                }
            }
        });


        userpicimg.setImageUrl(AppController.server
                        + "f_get_user_img_profile_viewer.php?userId="
                        + AppController.getInstance().getUserId() + "&sessionNumber="
                        + AppController.getInstance().getSessionNumber()
                        + "&userIdImage=" + AppController.getInstance().getUserId(),
                NetworkHandler.getImageLoader());

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getDefaultTracker().setScreenName("Edit User Profile Screen");
        AppController.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private int getCountryNameArrayIndex(String country) {

        int i = 0;
        String[] countries = getResources().getStringArray(
                R.array.arab_countries_en_symbols);

        for (String countryname : countries) {
            if (countryname.equalsIgnoreCase(country))
                return i;
            i++;
        }
        return 0;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                selectedImagePath = PicturesUtils.getPath(getActivity(), selectedImageUri);
                Bitmap resizedBitmap = null;
                try {
                    resizedBitmap = PicturesUtils.resize(BitmapFactory.decodeFile(selectedImagePath), userpicimg.getWidth(), userpicimg.getHeight());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (resizedBitmap != null) {
                    userpicimg.setImageBitmap(resizedBitmap);
                    saveNewUserPic();
                } else {
                    Alerts.showError(R.string.error_title, R.string.image_error);
                }
            }
        }
    }

    private void saveNewUserPic() {

        if (selectedImagePath == null || selectedImagePath.length() <= 0) {
            // userpicimg.setError(getString(R.string.pleaseenterusername));
        } else {

            Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            // added lines
            bm.recycle();
            bm = null;
            // added lines
            byte[] b = baos.toByteArray();
            final String b64 = Base64.encodeToString(b, Base64.NO_WRAP);

//            Alerts.showProgressDialog();

            NetworkHandler.execute(
                    new StringRequest(Method.POST,
                            AppController.server + "f_upload_user_profile_img.php",

                            new StringResponseHandler<String>() {
                                @Override
                                public void handleRespone(String response) {
//                                    Alerts.hideProgressDialog();
                                    try {
                                        Log.v("test", response.toString());
                                        JSONObject resObj = new JSONObject(response);
                                        if (resObj.getInt("resultId") == 9000) {
                                            Alerts.showSuccessMessage();
//

                                        } else {
                                            Alerts.showError(resObj.getString("resultMessage"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new ErrorHandler()

                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("userId", AppController.getInstance().getUserId());
                            params.put("sessionNumber", AppController.getInstance().getSessionNumber());
                            params.put("imgExtention", "JPG");
                            params.put("imageBase64", b64);

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type",
                                    "application/x-www-form-urlencoded");
                            return params;
                        }
                    },
                    false,
                    true)
            ;
        }
    }

    private void saveNewUserName() {

        if (usernameet.getText().toString() == null || usernameet.getText().toString().length() <= 0) {
            usernameet.setError(getString(R.string.pleaseenternickname));
        } else {


            NetworkHandler.execute(
                    new StringRequest(Method.POST,
                            AppController.server + "f_update_user_nick_name.php",

                            new StringResponseHandler<String>() {
                                @Override
                                public void handleRespone(String response) {
                                    try {
                                        JSONObject resObj = new JSONObject(response);
                                        if (resObj.getInt("resultId") == 9000) {
                                            Alerts.showSuccessMessage();
                                        } else {
                                            Alerts.showError(resObj.getString("resultMessage"));
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new ErrorHandler()
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("userId", AppController.getInstance()
                                    .getUserId());
                            params.put("sessionNumber", AppController.getInstance()
                                    .getSessionNumber());
                            params.put("nickname", usernameet.getText().toString());

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type",
                                    "application/x-www-form-urlencoded");
                            return params;
                        }
                    },
                    false,true
            );

        }
    }

    private void saveNewUserEmail() {

        if (useremailet.getText().toString() == null || useremailet.getText().toString().length() <= 0) {
            useremailet.setError(getString(R.string.pleaseenteremail));
        } else {

            String url = AppController.server
                    + "f_update_user_email.php?userId="
                    + AppController.getInstance().getUserId() + "&sessionNumber="
                    + AppController.getInstance().getSessionNumber() + "&email=" + useremailet.getText().toString();

            Log.v("test", url);

//            Alerts.showProgressDialog();

            NetworkHandler.execute(url, null,this::handleSaveNewUserEmailResponse,
                    new ErrorHandler(),
                    false,true
            );

        }
    }

    private void handleSaveNewUserEmailResponse(JSONObject response) {
        try {
            if (response.getInt("resultId") == 9000) {
                AppController.getInstance().setEmail(useremailet.getText().toString());
                Alerts.showSuccessMessage();
            } else {
                Alerts.showError(response.getString("resultMessage"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNewUserPassword() {

        password = userpwet.getText().toString();

        if (password == null || userpwet.getText().toString().length() <= 0) {
            userpwet.setError(getString(R.string.pleaseenterpassword));
        } else {

            String url = AppController.server
                    + "f_update_user_password.php?userId=" + AppController.getInstance().getUserId()
                    + "&sessionNumber=" + AppController.getInstance().getSessionNumber()
                    + "&password=" + password;

//            Alerts.showProgressDialog();

            NetworkHandler.execute(url, null,this::handleSaveNewUserPasswordResponse,
                    new ErrorHandler(),
                    false,
                    true
            );


        }

    }

    private void handleSaveNewUserPasswordResponse(JSONObject response) {
        try {
//                                Alerts.hideProgressDialog();
            if (response.getInt("resultId") == 9000) {
                AppController.getInstance().saveLoginPassword(password);
                Alerts.showSuccessMessage();
            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
//                                Alerts.hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void saveNewUserInfo() {

        int showage = agerg.getCheckedRadioButtonId() == R.id.showagecb ? 1 : 2;
        int showsex = sexrg.getCheckedRadioButtonId() == R.id.showsexcb ? 1 : 2;
        int showcountry = countryrg.getCheckedRadioButtonId() == R.id.showcountrycb ? 1 : 2;
        String sex = sexvaluerg.getCheckedRadioButtonId() == R.id.femalerb ? "2" : "1";
        String country = getResources().getStringArray(R.array.arab_countries_en_symbols)[countrysp.getSelectedItemPosition()];

        String url = AppController.server + "f_update_user_profile.php?userId="
                + AppController.getInstance().getUserId() + "&sessionNumber="
                + AppController.getInstance().getSessionNumber()
                + "&date_of_birth=" + agesp.getText().toString() + "&gender="
                + sex + "&country=" + country + "&show_birth_date=" + showage
                + "&show_gender=" + showsex + "&show_country=" + showcountry;

        Log.v("test", url);

        NetworkHandler.execute(url, null,this::handleSaveNewUserInfoResponse,
                new ErrorHandler(),
                false,
                true
        );
    }

    private void handleSaveNewUserInfoResponse(JSONObject response) {
        Log.v("test", response.toString());
        try {
            if (response.getInt("resultId") == 9000) {
                Alerts.showSuccessMessage();

            } else {
                Alerts.showError(response.getString("resultMessage"));
            }
        } catch (Exception e) {
//                            Alerts.hideProgressDialog();
            e.printStackTrace();
        }
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar birthdayCalendar = Calendar.getInstance();

        birthdayCalendar.set(Calendar.DAY_OF_MONTH, day);
        birthdayCalendar.set(Calendar.MONTH, month);
        birthdayCalendar.set(Calendar.YEAR, year);

        StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append(year).append("-");
        if (month < 10)
            dateBuilder.append("0");
        dateBuilder.append(month + 1).append("-");
        if (day < 10)
            dateBuilder.append("0");
        dateBuilder.append(day);

        agesp.setText(dateBuilder.toString().toString());
    }

}
