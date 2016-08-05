package edu.pdx.anb2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;

import edu.pdx.anb2.bluetooth.BluetoothApplicationState;
import edu.pdx.anb2.bluetooth.BluetoothMessages;
import edu.pdx.anb2.bluetooth.BluetoothService;
import edu.pdx.anb2.illustration.Illustration;

public class AdultModeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TOAST_TAG = AdultModeActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 23833;
    private BluetoothService bluetooth;
    private Toast lastToast;
    private int currentIllustration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adult_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        // setup message handler
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothMessages.SYNC_TAG:
                        onReceivedSync((BluetoothApplicationState) msg.obj);
                        break;
                    case BluetoothMessages.TOAST_TAG:
                        toast((String) msg.obj);
                        break;
                    default:
                        Log.w(TOAST_TAG, "Unknown message type to handle");
                }
            }
        };

        bluetooth = BluetoothService.getInstance(mHandler);
        changeContentPanel(R.layout.content_presentation_mode);
        setupPresentationWidgets();
    }

    private int currentIllustration() {
        return currentIllustration;
    }

    private void sendSync(BluetoothApplicationState state) {
        bluetooth.sendSync(state);
    }

    void setupPresentationWidgets() {
        populateImageSlider(R.id.imagePicker);
        setupSliderHideCheckbox();
        setupChildModeApprovalButton();
    }

    private void onReceivedSync(BluetoothApplicationState obj) {
        changeChildModeIllustration(obj.illustration);
        changeChildModeSuccess(obj.success);
    }

    void changeChildModeIllustration(int image) {
        ImageView childModeView = (ImageView) findViewById(R.id.childModeView);
        assert childModeView != null;
        childModeView.setImageResource(image);
        currentIllustration = image;
    }

    private void changeChildModeSuccess(boolean success) {
        final ImageView approvalButton = (ImageView) findViewById(R.id.childModeApprovalButton);
        assert approvalButton != null;

        RelativeLayout canvas = (RelativeLayout) findViewById(R.id.childModeCanvas);
        assert canvas != null;

        if (success) {
            ColorDrawable backgroundColor = new ColorDrawable(getResources().getColor(R.color.pagingEnabledFaded));
            canvas.setBackgroundDrawable(backgroundColor);

            int buttonColor = getResources().getColor(R.color.success);
            approvalButton.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            ColorDrawable backgroundColor = new ColorDrawable(getResources().getColor(R.color.pagingDisabledFaded));
            canvas.setBackgroundDrawable(backgroundColor);

            approvalButton.clearColorFilter();
        }
    }

    private void toast(String message) {
        if (lastToast != null) lastToast.cancel();
        lastToast = Toast.makeText(AdultModeActivity.this, message, Toast.LENGTH_SHORT);
        lastToast.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adult_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navPresentation) {
            changeContentPanel(R.layout.content_presentation_mode);
            setupPresentationWidgets();
        } else if (id == R.id.navChildMode) {
            WidgetHelper.goTo(this, ChildModeActivity.class);
        } else if (id == R.id.navPair) {
            changeContentPanel(R.layout.content_pair);
            setupBluetoothWidgets();
        } else if (id == R.id.navPasscode) {
            changeContentPanel(R.layout.content_passcode);
        } else if (id == R.id.navCategory) {
            changeContentPanel(R.layout.content_category);
        } else if (id == R.id.navLevel) {
            changeContentPanel(R.layout.content_level);
        } else if (id == R.id.navHelp) {
            changeContentPanel(R.layout.content_help);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void changeContentPanel(int contentLayout) {
        FrameLayout contentPanel = (FrameLayout) findViewById(R.id.contentPanel);
        assert contentPanel != null;
        contentPanel.removeAllViews();
        contentPanel.addView(View.inflate(this, contentLayout, null));
    }

    void populateImageSlider(int imageSliderLayout) {
        LinearLayout imageSlider = (LinearLayout) findViewById(imageSliderLayout);
        assert imageSlider != null;
        imageSlider.removeAllViews();

        for (Illustration i : Illustration.ALL) {
            ImageView image = new ImageView(this);

            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(256, 256);
            params.setMargins(0, 0, 8, 0);
            image.setLayoutParams(params);
            image.setBackgroundResource(R.drawable.border);
            image.setImageResource(i.image);

            image.setClickable(true);
            image.setOnClickListener(new NextImageOnClickListener(i));

            imageSlider.addView(image);
        }
    }

    private class NextImageOnClickListener implements View.OnClickListener {
        private final Illustration i;

        public NextImageOnClickListener(Illustration i) {
            this.i = i;
        }

        @Override
        public void onClick(View v) {
            changeChildModeIllustration(i.image);
            changeChildModeSuccess(false);
            sendSync(new BluetoothApplicationState(i.image, false));
        }
    }

    void setupBluetoothWidgets() {
        setupBluetoothEnableButton();
        final Spinner devices = populateBluetoothSpinner();
        setupBluetoothConnectButton(devices);
    }

    Button setupBluetoothEnableButton() {
        Button enableBluetoothButton = (Button) findViewById(R.id.enableBluetoothButton);
        assert enableBluetoothButton != null;

        if (bluetooth.adapter() == null) {
            enableBluetoothButton.setText(R.string.no_bluetooth);
            return enableBluetoothButton;
        }

        if (!bluetooth.adapter().isEnabled()) {
            enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            });
            enableBluetoothButton.setEnabled(true);
        } else {
            enableBluetoothButton.setText(R.string.enabled);
            enableBluetoothButton.setEnabled(false);
        }

        return enableBluetoothButton;
    }

    Spinner populateBluetoothSpinner() {
        final Spinner devicesSpinner = (Spinner) findViewById(R.id.devicesSpinner);
        assert devicesSpinner != null;
        final ArrayAdapter<DeviceSpinnerItem> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);

        if (bluetooth.adapter() != null) {
            Set<BluetoothDevice> pairedDevices = bluetooth.adapter().getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                spinnerArrayAdapter.add(new DeviceSpinnerItem(device));
            }
        }
        devicesSpinner.setAdapter(spinnerArrayAdapter);

        return devicesSpinner;
    }

    Button setupBluetoothConnectButton(final Spinner devices) {
        Button connectButton = (Button) findViewById(R.id.connectButton);
        assert connectButton != null;

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.start();
                DeviceSpinnerItem item = (DeviceSpinnerItem) devices.getAdapter().getItem(devices.getSelectedItemPosition());
                bluetooth.connect(item.device, false);
            }
        });

        return connectButton;
    }

    void setupChildModeApprovalButton() {
        final ImageView approvalButton = (ImageView) findViewById(R.id.childModeApprovalButton);
        assert approvalButton != null;
        approvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChildModeSuccess(true);
                sendSync(new BluetoothApplicationState(currentIllustration(), true));
            }
        });
    }

    void setupSliderHideCheckbox() {
        CheckBox chooseIllustrationCheckbox = (CheckBox) findViewById(R.id.chooseIllustrationsCheckbox);
        assert chooseIllustrationCheckbox != null;
        chooseIllustrationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout imageSlider = (LinearLayout) findViewById(R.id.imagePicker);
                assert imageSlider != null;
                imageSlider.setVisibility(isChecked ? LinearLayout.VISIBLE : LinearLayout.GONE);
            }
        });
    }
}