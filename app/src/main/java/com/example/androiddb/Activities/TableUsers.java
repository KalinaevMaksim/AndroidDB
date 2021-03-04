package com.example.androiddb.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androiddb.Entities.App;
import com.example.androiddb.Entities.AppDatabase;
import com.example.androiddb.Entities.Users.Users;
import com.example.androiddb.Entities.Users.UsersDao;
import com.example.androiddb.R;

import java.util.List;

public class TableUsers extends AppCompatActivity {
    TableLayout Table;
    AppDatabase database;
    UsersDao usersDao;
    List<Users> users;
    Thread SecondThread;
    TableRow.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_users);

        Table = findViewById(R.id.Table);

        layoutParams = getLayoutParams();

        InitDB();

        FillTable();
    }

    private void InitDB() {
        SecondThread = new Thread(() ->
        {
            database = App.getInstance().getDatabase();
            usersDao = database.usersDao();

            users = usersDao.GetAll();
        });
        SecondThread.start();

        try {
            SecondThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void FillTable() {
        for(int i = 0; i < users.size(); i++)
        {
            TableRow row = CreateNewRow();

            String[] UserAttributes = GetUserAttributes(i);

            FillNewRow(UserAttributes, row);

            Table.addView(row);
        }
    }

    private TableRow CreateNewRow() {
        return new TableRow(this);
    }

    private String[] GetUserAttributes(int i) {
        return new String[]
                        {
                                users.get(i).getLogin(),
                                users.get(i).getPassword(),
                                users.get(i).getPhone(),
                                users.get(i).getEmail(),
                                users.get(i).getLastName(),
                                users.get(i).getFirstName(),
                                users.get(i).getMiddleName(),
                                users.get(i).getRole()
                        };
    }

    private void FillNewRow(String[] userAttributes, TableRow row) {
        for (String userAttribute : userAttributes) {
            Context ThemeContext = getStyle();

            TextView UserAttribute = CreateTextView(ThemeContext);
            UserAttribute.setText(userAttribute);

            UserAttribute.setLayoutParams(layoutParams);
            row.addView(UserAttribute);
        }
    }

    private Context getStyle() {
        return new ContextThemeWrapper(this, R.style.TableTextView);
    }

    private TextView CreateTextView(Context themeContext) {
        return new TextView(themeContext);
    }

    private TableRow.LayoutParams getLayoutParams() {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams
                (
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1
                );
        layoutParams.setMargins(5, 5, 5, 5);
        return layoutParams;
    }
}