package com.ustdev.dalailulkhairat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChapterAdapter : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {
    private val chapters = mutableListOf<Chapter>()

    class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberText: TextView = itemView.findViewById(R.id.chapterNumber)
        val titleText: TextView = itemView.findViewById(R.id.chapterTitle)
        val titleArabicText: TextView = itemView.findViewById(R.id.chapterTitleArabic)
        val pageText: TextView = itemView.findViewById(R.id.pageNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ChapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.numberText.text = chapter.number.toString()
        holder.titleText.text = chapter.title
        holder.titleArabicText.text = "(${chapter.arabicTitle})"
        holder.pageText.text = chapter.page.toString()

        // Tambahkan click listener
        holder.itemView.setOnClickListener {
            val activity = holder.itemView.context as? MainActivity
            activity?.showWebView(chapter.page)
        }
    }

    override fun getItemCount() = chapters.size

    fun setChapters(newChapters: List<Chapter>) {
        chapters.clear()
        chapters.addAll(newChapters)
        notifyDataSetChanged()
    }
}