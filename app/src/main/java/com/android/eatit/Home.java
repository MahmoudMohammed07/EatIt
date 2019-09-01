package com.android.eatit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.eatit.Common.Common;
import com.android.eatit.Interface.ItemOnClickListener;
import com.android.eatit.Model.Category;
import com.android.eatit.Model.Token;
import com.android.eatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recyclerMenu;
    LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(Home.this)) {
                    loadMenu();
                } else {
                    Toast.makeText(Home.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(Home.this)) {
                    loadMenu();
                } else {
                    Toast.makeText(Home.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        Paper.init(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        recyclerMenu = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerMenu.setLayoutManager(layoutManager);

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Token");
        Token data = new Token(token, false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.menuImageView);
                final Category clickItem = model;
                viewHolder.setItemOnClickListener(new ItemOnClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodListIntent = new Intent(Home.this, FoodList.class);
                        foodListIntent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodListIntent);
                    }
                });
            }
        };
        adapter.startListening();
        recyclerMenu.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.refresh) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {

            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {

            Intent ordersIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(ordersIntent);

        } else if (id == R.id.nav_log_out) {

            Paper.book().destroy();

            Intent logoutIntent = new Intent(Home.this, SignIn.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);

        } else if (id == R.id.nav_change_pwd) {
            showChangePasswordDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Change Password");
        builder.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layoutPwd = inflater.inflate(R.layout.change_password_layout, null);

        final MaterialEditText editPassword = (MaterialEditText) layoutPwd.findViewById(R.id.editPassword);
        final MaterialEditText editNewPassword = (MaterialEditText) layoutPwd.findViewById(R.id.editNewPassword);
        final MaterialEditText editRepeatPassword = (MaterialEditText) layoutPwd.findViewById(R.id.editRepeatPassword);

        builder.setView(layoutPwd);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                if (editPassword.getText().toString().equals(Common.currentUser.getPassword())) {

                    if (editNewPassword.getText().toString().equals(editRepeatPassword.getText().toString())) {

                        Map<String, Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password", editNewPassword.getText().toString());

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Common.currentUser.setPassword(editNewPassword.getText().toString());
                                        Toast.makeText(Home.this, "Password has been changed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Wrong old password!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }
}
