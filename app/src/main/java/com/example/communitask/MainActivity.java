package com.example.communitask;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.communitask.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment is missing from activity_main.xml");
        }

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            boolean showBottomNavigation = destinationId == R.id.taskFeedFragment
                    || destinationId == R.id.myTasksFragment
                    || destinationId == R.id.profileFragment;

            binding.bottomNavigation.setVisibility(showBottomNavigation ? View.VISIBLE : View.GONE);
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });
    }
}
