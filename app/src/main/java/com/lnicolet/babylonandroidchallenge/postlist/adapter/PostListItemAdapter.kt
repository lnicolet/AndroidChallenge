package com.lnicolet.babylonandroidchallenge.postlist.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lnicolet.babylonandroidchallenge.R
import com.lnicolet.babylonandroidchallenge.core.GlideApp
import com.lnicolet.babylonandroidchallenge.core.gone
import com.lnicolet.babylonandroidchallenge.core.visible
import com.lnicolet.babylonandroidchallenge.postlist.models.Post
import kotlinx.android.synthetic.main.view_list_item_post.view.*

/**
 * Created by Luca Nicoletti
 * on 15/04/2019
 */

class PostListItemAdapter(
    private var postList: List<Post>,
    private val postClickListener: OnPostClickListener
) : RecyclerView.Adapter<PostListItemAdapter.ViewHolder>() {

    interface OnPostClickListener {
        fun onPostClicked(
            post: Post,
            imageView: View? = null,
            title: View? = null,
            body: View? = null
        )
    }

    fun updatePosts(newList: List<Post>) {
        this.postList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_item_post, parent, false)
        )

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        holder.itemView.tv_title.text = post.title
        holder.itemView.tv_partial_body.text = post.body
        holder.itemView.fl_post_item_container.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                postClickListener.onPostClicked(
                    post,
                    imageView = holder.itemView.cv_image_container,
                    title = holder.itemView.tv_title,
                    body = holder.itemView.tv_partial_body
                )
            } else {
                postClickListener.onPostClicked(post)
            }
        }
        post.user?.let { postCreator ->
            holder.itemView.g_user_info.visible()
            GlideApp.with(holder.itemView)
                .load(postCreator.imageUrl)
                .into(holder.itemView.iv_user_picture)
            holder.itemView.tv_user_name.text = postCreator.userName
        } ?: run {
            holder.itemView.g_user_info.gone()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}