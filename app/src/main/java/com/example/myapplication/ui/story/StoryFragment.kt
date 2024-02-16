package com.example.myapplication.ui.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentStoryBinding
import com.example.myapplication.ui.LoadingStateAdapter
import com.example.myapplication.ui.dialog.CustomDialogFragment
import com.example.myapplication.ui.viewmodel.StoryViewModel
import com.example.myapplication.ui.viewmodel.ViewModelFactory

class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding
    private val storyViewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvListStory.layoutManager = layoutManager

        getData()

        storyViewModel.isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        storyViewModel.isEmpty.observe(viewLifecycleOwner){
            showEmpty(it)
        }

        storyViewModel.isError.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                val dialogFragment = CustomDialogFragment.newInstance(it)
                dialogFragment.show(childFragmentManager, CustomDialogFragment::class.java.simpleName)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showEmpty(state: Boolean) {
        binding.tvNoStory.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun getData() {
        val adapter = ListStoryAdapter()
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        storyViewModel.story.observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }
    }
}