package com.example.androiddb.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androiddb.Entities.App;
import com.example.androiddb.Entities.AppDatabase;
import com.example.androiddb.Entities.Users.Users;
import com.example.androiddb.Entities.Users.UsersDao;
import com.example.androiddb.R;

import java.util.List;

public class Registration extends AppCompatActivity {
    EditText Login, Password, RepeatPassword, Phone, Email, LastName, FirstName, MiddleName;
    AppDatabase database;
    UsersDao usersDao;
    List<Users> users;
    Thread SecondThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Login = findViewById(R.id.edtxtLogin);
        Password = findViewById(R.id.edtxtPassword);
        RepeatPassword = findViewById(R.id.edtxtRepeatPassword);
        Phone = findViewById(R.id.edtxtPhone);
        Email = findViewById(R.id.edtxtEmail);
        LastName = findViewById(R.id.edtxtLastName);
        FirstName = findViewById(R.id.edtxtFirstName);
        MiddleName = findViewById(R.id.edtxtMiddleName);

        Runnable InitDB = () ->
        {
            database = App.getInstance().getDatabase();
            usersDao = database.usersDao();

            users = usersDao.GetAll();
        };
        SecondThread = new Thread(InitDB);
        SecondThread.start();

        try {
            SecondThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void RegUser(View view)
    {
        String[] UserAttributes = new String[]
            {
                    Login.getText().toString(),
                    Login.getTag().toString(),

                    Password.getText().toString(),
                    Password.getTag().toString(),

                    RepeatPassword.getText().toString(),
                    RepeatPassword.getTag().toString(),

                    Phone.getText().toString(),
                    Phone.getTag().toString(),

                    Email.getText().toString(),
                    Email.getTag().toString(),

                    LastName.getText().toString(),
                    LastName.getTag().toString(),

                    FirstName.getText().toString(),
                    FirstName.getTag().toString(),

                    MiddleName.getText().toString(),
                    MiddleName.getTag().toString()
            };

        if(!CheckFields(UserAttributes))
            return;

        if(!CheckExcistedUser(UserAttributes[0]))
            return;

        if(!CheckRepeatPassword(UserAttributes[1], UserAttributes[2]))
            return;

        AddNewUser(UserAttributes);

        GoBack();
    }

    private boolean CheckRepeatPassword(String password, String repeatPassword) {
        if(repeatPassword.equals(password))
            return true;

        Toast.makeText(this, "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void GoBack() {
        onBackPressed();
    }

    private void AddNewUser(String[] UserAttributes) {
        Users user = new Users
                (
                        UserAttributes[0],
                        UserAttributes[1],
                        UserAttributes[3],
                        UserAttributes[4],
                        UserAttributes[5],
                        UserAttributes[6],
                        UserAttributes[7]
                );

        Runnable AddUser = () ->
            usersDao.Insert(user);

        SecondThread = new Thread(AddUser);
        SecondThread.start();

        try {
            SecondThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean CheckFields(String[] UserAttributes)
    {
        StringBuilder Fields = new StringBuilder();

        for (int i = 0; i < UserAttributes.length; i += 2) {
            if (!UserAttributes[i].equals(""))
                continue;

            if (Fields.length() > 0)
                Fields.append(", ");

            Fields.append(UserAttributes[i+1]);
        }

        if(Fields.length() == 0)
            return true;

        Toast.makeText(this, "Заполните поля: " + Fields, Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean CheckExcistedUser(String login)
    {
        if(!FindUser(login))
            return true;

        Toast.makeText(this, "Пользователь с таким логином уже существует",
                Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean FindUser(String login) {
        for(int i = 0; i < users.size(); i++)
        {
            if(users.get(i).getLogin().equals(login))
                return true;
        }
        return false;
    }
}