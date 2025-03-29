package com.ustdev.dalailulkhairat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookmarkAdapter(
    private val bookmarks: MutableList<Int>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    private val chapters = listOf(
        Chapter(1, "Muqoddimah", "المقدمة", 1, 1),
        Chapter(2, "Hari Senin", "يوم الإثنين", 25, 25),
        Chapter(3, "Hari Selasa", "يوم الثلاثاء", 45, 45),
        Chapter(4, "Hari Rabu", "يوم الأربعاء", 65, 65),
        Chapter(5, "Hari Kamis", "يوم الخميس", 88, 88),
        Chapter(6, "Hari Jumat", "يوم الجمعة", 110, 110),
        Chapter(7, "Hari Sabtu", "يوم السبت", 136, 136),
        Chapter(8, "Hari Ahad", "يوم الأحد", 161, 161),
        Chapter(9, "Do'a", "الدعاء", 197, 197)
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val pageNumberText: TextView = view.findViewById(R.id.pageNumberText)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pageNumber = bookmarks[position]
        val chapterInfo = getChapterInfo(pageNumber)

        holder.titleText.text = "${chapterInfo.title} (${chapterInfo.arabicTitle})"
        holder.pageNumberText.text = "Halaman ${pageNumber}"

        holder.itemView.setOnClickListener {
            onItemClick(pageNumber+1)
            true
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(pageNumber)
            true
        }
    }

    private fun getChapterInfo(pageNumber: Int): Chapter {
        return chapters.findLast { chapter ->
            pageNumber >= chapter.page
        } ?: chapters.first()
    }

    override fun getItemCount() = bookmarks.size

    fun updateBookmarks(newBookmarks: Set<Int>) {
        bookmarks.clear()
        bookmarks.addAll(newBookmarks.sorted())
        notifyDataSetChanged()
    }
}