package br.edu.ifsp.scl.sc3043959.postviewer.ui.composable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.edu.ifsp.scl.sc3043959.postviewer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit
) {
    // Barra superior compartilhada por todas as telas do app.
    // O titulo usa stringResource para reaproveitar o nome definido em strings.xml.
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {
            // O botao de voltar aparece apenas quando a tela atual não é a tela inicial.
            if (canNavigateBack) {
                TextButton(onClick = onNavigateBack) {
                    Text(text = "Voltar")
                }
            }
        }
    )
}
