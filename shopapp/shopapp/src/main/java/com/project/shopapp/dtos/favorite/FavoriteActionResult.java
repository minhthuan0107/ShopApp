package com.project.shopapp.dtos.favorite;

import com.project.shopapp.models.Favorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteActionResult {
    private Favorite favorite;
    private boolean added;
}
