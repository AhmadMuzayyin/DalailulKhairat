package com.ustdev.dalailulkhairat

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookmarkFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var bookmarkManager: BookmarkManager
    private lateinit var adapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmarkManager = BookmarkManager(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.emptyView)

        setupRecyclerView()
        updateBookmarkList()
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter(
            bookmarks = mutableListOf(),
            onItemClick = { pageNumber ->
                (activity as? MainActivity)?.showWebView(pageNumber - 1)
            },
            onDeleteClick = { pageNumber ->
                bookmarkManager.removeBookmark(pageNumber)
                updateBookmarkList()
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@BookmarkFragment.adapter
        }
    }

    private fun updateBookmarkList() {
        val bookmarks = bookmarkManager.getBookmarks()
        adapter.updateBookmarks(bookmarks)

        if (bookmarks.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateBookmarkList()
    }

    fun refreshBookmarks() {
        updateBookmarkList()
    }
}