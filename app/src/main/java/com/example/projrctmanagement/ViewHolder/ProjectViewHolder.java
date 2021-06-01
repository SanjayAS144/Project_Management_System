package com.example.projrctmanagement.ViewHolder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projrctmanagement.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.ProgressIndicator;

import pl.droidsonroids.gif.GifImageView;

public class ProjectViewHolder extends RecyclerView.ViewHolder{

    public MaterialCardView project_card;
    public TextView project_name,progress;
    public ProgressIndicator progressBar;
    public GifImageView member_image1,member_image2,member_image3,member_image4;
    public MaterialCardView member1card,member2card,member3card,member4card;
    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        project_card = itemView.findViewById(R.id.project_card);
        project_name = itemView.findViewById(R.id.project_name);
        progress = itemView.findViewById(R.id.progress);
        member_image1 = itemView.findViewById(R.id.member_image1);
        member_image2 = itemView.findViewById(R.id.member_image2);
        member_image3 = itemView.findViewById(R.id.member_image3);
        member_image4 = itemView.findViewById(R.id.member_image4);
        member1card = itemView.findViewById(R.id.member1card);
        member2card = itemView.findViewById(R.id.member2card);
        member3card = itemView.findViewById(R.id.member3card);
        member4card = itemView.findViewById(R.id.member4card);
        progressBar = itemView.findViewById(R.id.progressBar);
    }
}
