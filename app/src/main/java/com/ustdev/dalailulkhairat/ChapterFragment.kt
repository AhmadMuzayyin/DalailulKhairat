package com.ustdev.dalailulkhairat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChapterFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val chapterAdapter = ChapterAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chapter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chapterRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chapterAdapter

        // Tambahkan data chapter
        val chapters = listOf(
            Chapter(1, "Muqoddimah", "المقدمة", 1,2),
            Chapter(2, "Hari Senin", "يوم الإثنين", 25,26),
            Chapter(3, "Hari Selasa", "يوم الثلاثاء", 45,46),
            Chapter(4, "Hari Rabu", "يوم الأربعاء", 65,66),
            Chapter(5, "Hari Kamis", "يوم الخميس", 88,89),
            Chapter(6, "Hari Jumat", "يوم الجمعة", 110,111),
            Chapter(7, "Hari Sabtu", "يوم السبت", 136,137),
            Chapter(8, "Hari Ahad", "يوم الأحد", 161,162),
            Chapter(9, "Do'a", "الدعاء", 197,198),
        )
        chapterAdapter.setChapters(chapters)
    }
}