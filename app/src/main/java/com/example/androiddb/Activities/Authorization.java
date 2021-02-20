package com.example.androiddb.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androiddb.Entities.App;
import com.example.androiddb.Entities.AppDatabase;
import com.example.androiddb.Entities.Users.Users;
import com.example.androiddb.Entities.Users.UsersDao;
import com.example.androiddb.R;

import java.util.List;

public class Authorization extends AppCompatActivity {
    EditText Login, Password;
    AppDatabase database;
    UsersDao usersDao;
    Thread SecondThread;
    List<Users> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        Login = findViewById(R.id.edtxtLogin);
        Password = findViewById(R.id.edtxtPassword);

        Runnable InitDB= () ->
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

        CheckDB();
    }

    public void OpenForm(View view)
    {
        String login = Login.getText().toString(), password = Password.getText().toString();

        if(!CheckFields(login, password)) {
            return;
        }

        Users user = FindUser(login, null);

        if(!CheckExistUser(user))
            return;

        if(!CheckPassword(user, password))
            return;

        LogIn(login);

        ClearFields();
    }

    private Users FindUser(String login, Users user) {
        for(int i = 0; i < users.size(); i++)
        {
            if(users.get(i).getLogin().equals(login))
                user = users.get(i);
        }
        return user;
    }

    private void ClearFields() {
        Login.setText("");
        Password.setText("");
    }

    private void LogIn(String login) {
        Intent intent = new Intent(Authorization.this, MainForm.class);
        if(login.equals("Admin"))
            intent.putExtra("Role", login);

        startActivity(intent);
    }

    boolean CheckFields(String login, String password)
    {
        if(login.equals(""))
        {
            Toast.makeText(this, password.equals("") ? "Заполните все поля" : "Заполните логин", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.equals(""))
        {
            Toast.makeText(this, "Заполните пароль", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void CheckDB()
    {
        if(users.size() > 0)
            return;

        FillDB();
    }

    void FillDB()
    {
        Users Admin = new Users();
        Admin.setLogin("Admin");
        Admin.setPassword("123");
        Admin.setPhone("7(764)059-44-98");
        Admin.setEmail("fodatipu-5632@yopmail.com");
        Admin.setLastName("Admin");
        Admin.setFirstName("Admin");
        Admin.setMiddleName("Admin");

        Users wer = new Users();
        wer.setLogin("wer");
        wer.setPassword("124");
        wer.setPhone("1(3685)549-50-09");
        wer.setEmail("elinnyllabe-3318@yopmail.com");
        wer.setLastName("wer");
        wer.setFirstName("wer");
        wer.setMiddleName("wer");

        Runnable AddBaseUsers= () ->
        {
            usersDao.Insert(Admin);
            usersDao.Insert(wer);
        };
        SecondThread = new Thread(AddBaseUsers);
        SecondThread.start();

        try {
            SecondThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean CheckExistUser(Users user)
    {
        if(user != null)
            return true;

        Toast.makeText(this, "Пользователь с таким логином не найден", Toast.LENGTH_LONG).show();
        return false;
    }

    boolean CheckPassword(Users user, String password)
    {
        if(user.getPassword().equals(password))
            return true;

        Toast.makeText(this, "Неверный пароль", Toast.LENGTH_LONG).show();
        return false;
    }

    public void OpenReg(View view)
    {
        Intent intent = new Intent(Authorization.this, Registration.class);
        startActivity(intent);
    }
}