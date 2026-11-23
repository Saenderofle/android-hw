package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun formatResult(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val firstNumber = findViewById<EditText>(R.id.firstNumber)
        val secondNumber = findViewById<EditText>(R.id.secondNumber)
        val addButton = findViewById<Button>(R.id.addButton)
        val subButton = findViewById<Button>(R.id.subtractButton)
        val mulButton = findViewById<Button>(R.id.multiplyButton)
        val divButton = findViewById<Button>(R.id.divideButton)
        val result = findViewById<TextView>(R.id.resultText)
        val clearButton = findViewById<Button>(R.id.clearButton)

        clearButton.setOnClickListener {
            firstNumber.text.clear()
            secondNumber.text.clear()
            result.text = "Результат: [число]"
        }

        addButton.setOnClickListener {
            val first = firstNumber.text.toString().toDoubleOrNull()
            val second = secondNumber.text.toString().toDoubleOrNull()

            if (first == null || second == null) {
                Log.w("MyTag", "Порожнє поле")
                result.text = "Введіть обидва числа"
            } else {
                Log.d("MyTag", "Додавання: $first + $second")
                result.text = "Результат: ${formatResult(first + second)}"
            }
        }

        subButton.setOnClickListener {
            val first = firstNumber.text.toString().toDoubleOrNull()
            val second = secondNumber.text.toString().toDoubleOrNull()

            if (first == null || second == null) {
                Log.w("MyTag", "Порожнє поле")
                result.text = "Введіть обидва числа"
            } else {
                Log.d("MyTag", "Віднімання: $first - $second")
                result.text = "Результат: ${formatResult(first - second)}"
            }
        }

        mulButton.setOnClickListener {
            val first = firstNumber.text.toString().toDoubleOrNull()
            val second = secondNumber.text.toString().toDoubleOrNull()

            if (first == null || second == null) {
                Log.w("MyTag", "Порожнє поле")
                result.text = "Введіть обидва числа"
            } else {
                Log.d("MyTag", "Множення: $first * $second")
                result.text = "Результат: ${formatResult(first * second)}"
            }
        }

        divButton.setOnClickListener {
            val first = firstNumber.text.toString().toDoubleOrNull()
            val second = secondNumber.text.toString().toDoubleOrNull()

            if (first == null || second == null) {
                Log.w("MyTag", "Порожнє поле")
                result.text = "Введіть обидва числа"
            } else if (second == 0.0) {
                Log.w("MyTag", "Спроба ділення на нуль: $first / $second")
                result.text = "Помилка: ділення на нуль неможливе!"
            } else {
                Log.d("MyTag", "Ділення: $first / $second")
                result.text = "Результат: ${formatResult(first / second)}"
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
