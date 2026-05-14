package com.example.communityeventmanagement.features.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.util.ImagePickerBox
import com.example.communityeventmanagement.util.toDateString
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    communityId: Int,
    onCreateSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateEventViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return utcTimeMillis >= calendar.timeInMillis
            }
        }
    )

    if (viewModel.showSuccessSheet) {
        ModalBottomSheet(onDismissRequest = onCreateSuccess) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.msg_event_created_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(stringResource(R.string.msg_event_created_body), textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(32.dp))
                Button(onClick = onCreateSuccess, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.status_finished)) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_event), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Upload Gambar
            ImagePickerBox(
                imageUri = viewModel.coverImageUri,
                onImageSelected = { viewModel.coverImageUri = it },
                label = stringResource(R.string.btn_choose_cover)
            )

            // Judul
            CreateInput(label = stringResource(R.string.label_event_name), value = viewModel.title, onValueChange = { viewModel.title = it }, icon = Icons.AutoMirrored.Filled.EventNote)
            
            // Kategori
            Text(stringResource(R.string.label_choose_category), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.categoryOptions.take(3).forEach { cat ->
                    FilterChip(
                        selected = viewModel.category == cat,
                        onClick = { viewModel.category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            // Tanggal & Waktu
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { showDatePicker = true }) {
                    CreateInput(label = stringResource(R.string.label_date), value = viewModel.selectedDateMillis?.toDateString() ?: "", onValueChange = {}, icon = Icons.Default.CalendarToday, enabled = false)
                }
                Box(modifier = Modifier.weight(1f)) {
                    // Simple select time placeholder
                    CreateInput(label = stringResource(R.string.label_time), value = viewModel.time, onValueChange = { viewModel.time = it }, icon = Icons.Default.Schedule)
                }
            }

            if (!viewModel.isDateTimeValid && viewModel.selectedDateMillis != null) {
                Text(
                    text = "Tanggal & waktu harus lebih dari saat ini",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            CreateInput(label = stringResource(R.string.label_location), value = viewModel.location, onValueChange = { viewModel.location = it }, icon = Icons.Default.LocationOn)
            
            CreateInput(label = stringResource(R.string.label_event_description), value = viewModel.description, onValueChange = { viewModel.description = it }, icon = Icons.Default.Description, singleLine = false)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { scope.launch { viewModel.submit(communityId) } },
                enabled = viewModel.isFormValid && !viewModel.isSubmitting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (viewModel.isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(stringResource(R.string.btn_publish_event), fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false 
                }) { Text(stringResource(R.string.btn_choose)) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun CreateInput(
    label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector, enabled: Boolean = true, singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) }, enabled = enabled,
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = singleLine
    )
}
