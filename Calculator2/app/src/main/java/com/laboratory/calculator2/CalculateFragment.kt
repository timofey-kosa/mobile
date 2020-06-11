package com.laboratory.calculator2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_calculate.*
import java.math.BigDecimal
import java.math.RoundingMode

class CalculateFragment : Fragment() {
    private val RESULT_TAG = "resultFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendResultButton.setOnClickListener {
            // Проверка полей на наличие цифр
            if (number1.text.isNullOrBlank() || number2.text.isNullOrBlank()) {
                Toast.makeText(context, "Введите числа в поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Использование BigDecimal для более точных рассчётов
            val first = BigDecimal(number1.text.toString())
            val second = BigDecimal(number2.text.toString())

            // Выполнение операции
            val result = when {
                addButton.isChecked -> first.plus(second)
                subtractButton.isChecked -> first.minus(second)
                multiplyButton.isChecked -> first.multiply(second)
                // Деление с округлением для избежания ошибок и удаление лишних нулей
                divideButton.isChecked -> first.divide(second, 10, RoundingMode.HALF_UP).stripTrailingZeros()
                else -> BigDecimal.ZERO
            }
            sendResult(result)
        }
    }

    private fun sendResult(result: BigDecimal) {
        val fragment = ResultFragment(result)
        val transaction = activity!!.supportFragmentManager.beginTransaction()

        // Удаление предыдущего фрагмента с результатом, если он существует
        val previousResult = activity!!.supportFragmentManager.findFragmentByTag(RESULT_TAG)
        if (previousResult != null)
            transaction.remove(previousResult)

        // Добавление фрагмента к activity_main
        transaction.add(R.id.mainActivity, fragment, RESULT_TAG)
        transaction.commit()
    }
}
