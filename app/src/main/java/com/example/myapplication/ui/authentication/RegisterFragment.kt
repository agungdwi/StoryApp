package com.example.myapplication.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.ui.customview.EmailEditText
import com.example.myapplication.ui.dialog.CustomDialogFragment
import com.example.myapplication.ui.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.registerResult.observe(viewLifecycleOwner){
            if(it.message.isNotEmpty()){
                val dialogFragment = CustomDialogFragment.newInstance(getString(R.string.register_succes))
                dialogFragment.show(childFragmentManager, CustomDialogFragment::class.java.simpleName)
            }
        }

        authViewModel.isError.observe(viewLifecycleOwner){
            if(it == ERROR_400) {
                val dialogFragment = CustomDialogFragment.newInstance(getString(R.string.email_registered_error))
                dialogFragment.show(childFragmentManager, CustomDialogFragment::class.java.simpleName)
            }else{
                val dialogFragment = CustomDialogFragment.newInstance(it)
                dialogFragment.show(childFragmentManager, CustomDialogFragment::class.java.simpleName)
            }

        }

        authViewModel.isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        binding.registerButton.setOnClickListener {
            binding.apply {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                val isValidName = validateName(name)
                val isValidEmail = validateEmail(email)
                val isValidPassword = validatePassword(password)

                if (isValidName && isValidEmail && isValidPassword) {
                    authViewModel.registerResponse(name, email, password)
                }

            }

        }

        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.isNotEmpty()){
                    binding.edRegisterName.setBackgroundResource(R.drawable.custom_edit_text)
                }
            }
            override fun afterTextChanged(s: Editable) {
                if(s.isNotEmpty()){
                    binding.edRegisterName.setBackgroundResource(R.drawable.custom_edit_text)
                }
            }
        })

        binding.btnRegister.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
                addSharedElement(binding.tvRegister, "title")
                addSharedElement(binding.edRegisterEmail, "email")
                addSharedElement(binding.edRegisterPassword, "password")
                addSharedElement(binding.registerButton, "button")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }
    }

    private fun validateName(name: String): Boolean {
        if (name.isEmpty()) {
            binding.edRegisterName.setBackgroundResource(R.drawable.error_edit_text)
            binding.edRegisterName.error = getString(R.string.name_empty)
            return false
        }
        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.edRegisterEmail.setBackgroundResource(R.drawable.error_edit_text)
            binding.edRegisterEmail.error = getString(R.string.email_empty)
            return false
        }
        if (!email.matches(EmailEditText.emailPattern)) {
            return false
        }
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            binding.edRegisterPassword.setBackgroundResource(R.drawable.error_edit_text)
            binding.edRegisterPassword.error = getString(R.string.password_empty)
            return false
        }
        if (password.length < 8) {
            return false
        }
        return true
    }


    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object{
        private const val ERROR_400 = "ERROR 400 : Bad Request"
    }
}