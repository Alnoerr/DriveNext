package dev.alnoer.drivenext.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextButton
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme
import kotlinx.coroutines.launch

private val Pages = listOf(
    OnboardingPage(
        imageRes = R.drawable.onboarding_1,
        titleRes = R.string.onboarding_title_1,
        descriptionRes = R.string.onboarding_description_1,
        buttonNextTextRes = R.string.onboarding_next
    ),
    OnboardingPage(
        imageRes = R.drawable.onboarding_2,
        titleRes = R.string.onboarding_title_2,
        descriptionRes = R.string.onboarding_description_2,
        buttonNextTextRes = R.string.onboarding_next
    ),
    OnboardingPage(
        imageRes = R.drawable.onboarding_3,
        titleRes = R.string.onboarding_title_3,
        descriptionRes = R.string.onboarding_description_3,
        buttonNextTextRes = R.string.onboarding_finish
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { Pages.size }
    val scope = rememberCoroutineScope()

    Column(modifier.padding(vertical = 8.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            if (pagerState.currentPage != Pages.lastIndex) {
                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { index ->
            OnboardingPageContent(Pages[index])
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(Pages.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        Modifier.size(width = if (selected) 40.dp else 16.dp, height = 8.dp)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE4E7EC),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            DriveNextButton(
                onClick = {
                    if (pagerState.currentPage == Pages.lastIndex) {
                        onFinish()
                    } else if (!pagerState.isScrollInProgress) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.widthIn(max = 132.dp).heightIn(min = 50.dp)
            ) {
                Text(stringResource(Pages[pagerState.currentPage].buttonNextTextRes))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.onboarding_image_max_height))
        )
        Column(Modifier.padding(top = 32.dp, bottom = 16.dp)) {
            Text(stringResource(page.titleRes), style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))

            Text(stringResource(page.descriptionRes))
        }
    }
}

@PreviewScreenSizes
@Composable
private fun OnboardingScreenPreview() {
    DriveNextTheme {
        Surface {
            OnboardingScreen({})
        }
    }
}
