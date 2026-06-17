package com.example.communityeventmanagementsystem.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagementsystem.ui.theme.Dimens
import com.example.communityeventmanagementsystem.ui.theme.SurfaceVariant

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    val shimmerColors = listOf(
        SurfaceVariant.copy(alpha = 0.6f),
        SurfaceVariant.copy(alpha = 0.2f),
        SurfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun SkeletonEventCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SpacingMd)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingSm))
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
        )
    }
}

@Composable
fun SkeletonCommunityCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SpacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerEffect(
            modifier = Modifier.size(64.dp),
            shape = CircleShape
        )
        Spacer(modifier = Modifier.width(Dimens.SpacingMd))
        Column {
            ShimmerEffect(
                modifier = Modifier
                    .width(150.dp)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingSm))
            ShimmerEffect(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
            )
        }
    }
}

@Composable
fun SkeletonHome() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.ContainerPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerEffect(modifier = Modifier.size(40.dp), shape = CircleShape)
            ShimmerEffect(modifier = Modifier.width(100.dp).height(24.dp))
            ShimmerEffect(modifier = Modifier.size(40.dp), shape = CircleShape)
        }
        
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
            repeat(4) {
                ShimmerEffect(modifier = Modifier.width(80.dp).height(36.dp), shape = RoundedCornerShape(18.dp))
            }
        }
        
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(16.dp))
        
        repeat(2) {
            SkeletonEventCard()
        }
    }
}
