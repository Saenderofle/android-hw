package com.example.calculatornorm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatornorm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var firstNumber: Double? = null
    private var currentOperation: String? = null

    private var isEnteringSecondNumber = false

    private var startNewNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        binding.zeroButton.setOnClickListener {
            enterDigit("0")
        }

        binding.oneButton.setOnClickListener {
            enterDigit("1")
        }

        binding.twoButton.setOnClickListener {
            enterDigit("2")
        }

        binding.threeButton.setOnClickListener {
            enterDigit("3")
        }

        binding.fourButton.setOnClickListener {
            enterDigit("4")
        }

        binding.fiveButton.setOnClickListener {
            enterDigit("5")
        }

        binding.sixButton.setOnClickListener {
            enterDigit("6")
        }

        binding.sevenButton.setOnClickListener {
            enterDigit("7")
        }

        binding.eightButton.setOnClickListener {
            enterDigit("8")
        }

        binding.nineButton.setOnClickListener {
            enterDigit("9")
        }

        binding.dotButton.setOnClickListener {
            enterDot()
        }

        binding.addButton.setOnClickListener {
            selectOperation("+")
        }

        binding.subButton.setOnClickListener {
            selectOperation("-")
        }

        binding.mulButton.setOnClickListener {
            selectOperation("*")
        }

        binding.divButton.setOnClickListener {
            selectOperation("/")
        }

        binding.equalButton.setOnClickListener {
            calculateResult()
        }

        binding.CButton.setOnClickListener {
            clear()
        }

        binding.backButton.setOnClickListener {
            backspace()
        }

        Log.d(TAG, "Калькулятор запущено")
    }

    private fun enterDigit(digit: String) {
        if (
            startNewNumber ||
            binding.resultText.text.toString() == "0" ||
            binding.resultText.text.toString() == "Помилка"
        ) {
            binding.resultText.text = digit
            startNewNumber = false
        } else {
            binding.resultText.append(digit)
        }

        Log.d(TAG, "Введено цифру $digit, екран=${binding.resultText.text}")
    }

    private fun enterDot() {
        val text = binding.resultText.text.toString()

        if (startNewNumber) {
            binding.resultText.text = "0."
            startNewNumber = false
            Log.d(TAG, "Розпочато введення дробового числа")
        } else if (!text.contains(".")) {
            binding.resultText.append(".")
            Log.d(TAG, "Додано десяткову крапку, екран=${binding.resultText.text}")
        } else {
            Log.w(TAG, "Повторну десяткову крапку проігноровано, екран=$text")
        }
    }

    private fun selectOperation(operation: String) {
        val currentNumber = binding.resultText.text.toString().toDoubleOrNull()

        if (currentNumber == null) {
            Log.w(TAG, "Операцію $operation проігноровано: значення на екрані не є числом")
            return
        }

        if (
            firstNumber != null &&
            currentOperation != null &&
            isEnteringSecondNumber &&
            !startNewNumber
        ) {
            val result = calculate(
                firstNumber!!,
                currentNumber,
                currentOperation!!
            )

            if (result == null) {
                showError()
                return
            }

            firstNumber = result
            binding.resultText.text = formatNumber(result)
            Log.i(TAG, "Проміжний результат=${formatNumber(result)}")
        } else {
            firstNumber = currentNumber
        }

        currentOperation = operation
        isEnteringSecondNumber = true
        startNewNumber = true

        Log.d(TAG, "Обрано операцію $operation, перше число=$firstNumber")
    }

    private fun calculateResult() {
        if (firstNumber == null || currentOperation == null) {
            Log.w(TAG, "Натискання = проігноровано: операцію не обрано")
            return
        }

        if (startNewNumber) {
            Log.w(TAG, "Натискання = проігноровано: друге число не введено")
            return
        }

        val secondNumber = binding.resultText.text.toString().toDoubleOrNull()

        if (secondNumber == null) {
            Log.w(TAG, "Натискання = проігноровано: друге значення не є числом")
            return
        }

        val operation = currentOperation!!
        val first = firstNumber!!
        val result = calculate(first, secondNumber, operation)

        if (result == null) {
            showError()
            return
        }

        binding.resultText.text = formatNumber(result)
        firstNumber = result
        currentOperation = null
        isEnteringSecondNumber = false
        startNewNumber = true

        Log.i(TAG, "$first $operation $secondNumber = ${formatNumber(result)}")
    }

    private fun calculate(
        a: Double,
        b: Double,
        operation: String
    ): Double? {
        return when (operation) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> {
                if (b == 0.0) {
                    Log.e(TAG, "Спроба ділення на нуль: $a / $b")
                    null
                } else {
                    a / b
                }
            }

            else -> {
                Log.e(TAG, "Невідома операція: $operation")
                null
            }
        }
    }

    private fun clear() {
        firstNumber = null
        currentOperation = null
        isEnteringSecondNumber = false
        startNewNumber = true
        binding.resultText.text = "0"

        Log.d(TAG, "Калькулятор очищено")
    }

    private fun backspace() {
        if (startNewNumber) {
            Log.d(TAG, "Backspace проігноровано: нове число ще не вводиться")
            return
        }

        val text = binding.resultText.text.toString()

        if (text.length > 1) {
            binding.resultText.text = text.dropLast(1)
        } else {
            binding.resultText.text = "0"
            startNewNumber = true
        }

        Log.d(TAG, "Видалено останній символ, екран=${binding.resultText.text}")
    }

    private fun showError() {
        binding.resultText.text = "Помилка"
        firstNumber = null
        currentOperation = null
        isEnteringSecondNumber = false
        startNewNumber = true

        Log.e(TAG, "Помилка обчислення, стан калькулятора скинуто")
    }

    private fun formatNumber(number: Double): String {
        return if (number % 1.0 == 0.0) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }

    companion object {
        private const val TAG = "Calculator"
    }
}
