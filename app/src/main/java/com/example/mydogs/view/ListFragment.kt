package com.example.mydogs.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mydogs.R
import com.example.mydogs.databinding.FragmentListBinding
import com.example.mydogs.viewModel.ListViewModel

class ListFragment : Fragment() {
    private lateinit var viewModel: ListViewModel
    private var dogsListAdapter = DogsListAdapter(arrayListOf())
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_list, container, false)
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()
        binding.dogListRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }
        binding.refreshLayout.setOnRefreshListener {
            binding.dogListRv.visibility = View.GONE
            binding.listError.visibility = View.GONE
            binding.loadingBar.visibility = View.VISIBLE
            viewModel.refreshBypassCache()
            binding.refreshLayout.isRefreshing = false
        }

        observerViewModel()
    }

    fun observerViewModel() {
        viewModel.dogs.observe(viewLifecycleOwner, Observer { dogs ->
            dogs?.let {
                binding.dogListRv.visibility = View.VISIBLE
                dogsListAdapter.updateDogsList(dogs)
            }

        })
        viewModel.dogsLoaderError.observe(viewLifecycleOwner, Observer { isError ->
            isError?.let {
                binding.listError.visibility = if (it) View.VISIBLE else View.GONE

            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                binding.loadingBar.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    binding.listError.visibility = View.GONE
                    binding.dogListRv.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(ListFragmentDirections.actionListFragmentToSettingsFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}