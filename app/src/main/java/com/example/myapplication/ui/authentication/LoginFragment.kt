package com.example.myapplication.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.ui.customview.EmailEditText
import com.example.myapplication.ui.dialog.CustomDialogFragment
import com.example.myapplication.ui.story.MainActivity
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.LoginViewModel
import com.example.myapplication.ui.viewmodel.ViewModelFactory
import com.example.myapplication.utils.TokenProvider


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val authViewModel by viewModels<AuthViewModel>()
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment

        authViewModel.loginResult.observe(viewLifecycleOwner) {
            loginViewModel.saveUserPref(
                it.loginResult.token,
                it.loginResult.userId,
                it.loginResult.name,
                binding.edLoginEmail.text.toString()
            )
            TokenProvider.token = it.loginResult.token
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        authViewModel.isError.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (it == ERROR_401) {
                    val dialogFragment =
                        CustomDialogFragment.newInstance(getString(R.string.login_error))
                    dialogFragment.show(
                        childFragmentManager,
                        CustomDialogFragment::class.java.simpleName
                    )
                } else {
                    val dialogFragment = CustomDialogFragment.newInstance(it)
                    dialogFragment.show(
                        childFragmentManager,
                        CustomDialogFragment::class.java.simpleName
                    )
                }


            }
        }

        loginViewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            if (token != "token") {

                TokenProvider.token = token
                Log.e("token", TokenProvider.token)
                Intent(requireActivity(), MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            val isValidEmail = validateEmail(email)
            val isValidPassword = validatePassword(password)

            if (isValidEmail && isValidPassword) {
                authViewModel.loginResponse(email, password)
            }

        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                addSharedElement(binding.tvLogin, "title")
                addSharedElement(binding.edLoginEmail, "email")
                addSharedElement(binding.edLoginPassword, "password")
                addSharedElement(binding.loginButton, "button")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }

    }


    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.edLoginEmail.setBackgroundResource(R.drawable.error_edit_text)
            binding.edLoginEmail.error = getString(R.string.email_empty)
            return false
        }
        if (!email.matches(EmailEditText.emailPattern)) {
            return false
        }
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            binding.edLoginPassword.setBackgroundResource(R.drawable.error_edit_text)
            binding.edLoginPassword.error = getString(R.string.password_empty)
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

    companion object {
        private const val ERROR_401 = "ERROR 401 : Unauthorized"
        fun newInstance() = LoginFragment()
    }

}