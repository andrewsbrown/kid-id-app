package edu.pdx.anb2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;

import edu.pdx.anb2.bluetooth.Bluetooth;
import edu.pdx.anb2.illustration.Illustration;

public class AdultModeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ENABLE_BT = 23833;
    private Handler mHandler;
    private Bluetooth bluetooth;
    private Toast lastToast;

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
        navigationView.setNavigationItemSelectedListener(this);

        changeContentPanel(R.layout.content_presentation_mode);

        populateImageSlider(R.id.imagePicker);

        // check box hides image picker
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

        final ImageView approvalButton = (ImageView) findViewById(R.id.childModeApprovalButton);
        assert approvalButton != null;
        approvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int successColor = getResources().getColor(R.color.success);
                approvalButton.setColorFilter(successColor, PorterDuff.Mode.SRC_ATOP);
            }
        });

        // setup message handler
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                if (lastToast != null) lastToast.cancel();
                lastToast = Toast.makeText(AdultModeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT);
                lastToast.show();
            }
        };

        // setup bluetooth service
        bluetooth = new Bluetooth(this, mHandler);
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

    void changeChildModeView(int image) {
        ImageView childModeView = (ImageView) findViewById(R.id.childModeView);
        assert childModeView != null;
        childModeView.setImageResource(image);

        final ImageView approvalButton = (ImageView) findViewById(R.id.childModeApprovalButton);
        assert approvalButton != null;
        approvalButton.clearColorFilter();
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
            changeChildModeView(i.image);
        }
    }

    void setupBluetoothWidgets() {
        Button enableBluetoothButton = (Button) findViewById(R.id.enableBluetoothButton);
        assert enableBluetoothButton != null;

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            enableBluetoothButton.setText(R.string.no_bluetooth);
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
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

        // setup paired devices spinner
        final Spinner devicesSpinner = (Spinner) findViewById(R.id.devicesSpinner);
        assert devicesSpinner != null;
        final ArrayAdapter<DeviceItem> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        devicesSpinner.setAdapter(spinnerArrayAdapter);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            spinnerArrayAdapter.add(new DeviceItem(device));
        }

        // test connection
        Button connectButton = (Button) findViewById(R.id.connectButton);
        assert connectButton != null;
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.start();
                DeviceItem item = spinnerArrayAdapter.getItem(devicesSpinner.getSelectedItemPosition());
                bluetooth.connect(item.device, false);
            }
        });
    }

    private class DeviceItem {
        public final String id;
        public final String name;
        public final BluetoothDevice device;

        public DeviceItem(BluetoothDevice device) {
            this.device = device;
            this.id = device.getAddress();
            this.name = device.getName();
        }

        @Override
        public String toString() {
            return name + "\n" + id;
        }
    }
}
