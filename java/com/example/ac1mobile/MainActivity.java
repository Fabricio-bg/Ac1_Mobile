package com.fadev.ac1_mobile;

import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editTitulo, editDisciplina, editDataEntrega, editDescricao;
    Button btnSalvar;
    ListView listViewTarefas;
    Spinner spinnerPrioridade;
    CheckBox checkBoxConcluida;
    BancoHelper dbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaTarefas;
    ArrayList<Integer> listaIds;
    int idTarefaSelecionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new BancoHelper(this);

        editTitulo = findViewById(R.id.editTitulo);
        editDisciplina = findViewById(R.id.editDisciplina);
        editDataEntrega = findViewById(R.id.editDataEntrega);
        editDescricao = findViewById(R.id.editDescricao);
        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        checkBoxConcluida = findViewById(R.id.checkBoxConcluida);
        btnSalvar = findViewById(R.id.btnSalvar);
        listViewTarefas = findViewById(R.id.listViewTarefas);

        listaTarefas = new ArrayList<>();
        listaIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaTarefas);
        listViewTarefas.setAdapter(adapter);

        btnSalvar.setOnClickListener(v -> salvarNovaTarefa());

        Spinner spinnerFiltro = findViewById(R.id.spinnerFiltro);
        spinnerFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String filtroEscolhido = parent.getItemAtPosition(position).toString();

                carregarListaComFiltro(filtroEscolhido);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        listViewTarefas.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {

                idTarefaSelecionada = listaIds.get(position);

                Cursor cursor = dbHelper.buscarTarefaPorId(idTarefaSelecionada);

                if (cursor.moveToFirst()) {

                    editTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
                    editDisciplina.setText(cursor.getString(cursor.getColumnIndexOrThrow("disciplina")));
                    editDataEntrega.setText(cursor.getString(cursor.getColumnIndexOrThrow("data_entrega")));
                    editDescricao.setText(cursor.getString(cursor.getColumnIndexOrThrow("descricao")));

                    String status = cursor.getString(cursor.getColumnIndexOrThrow("concluida"));
                    checkBoxConcluida.setChecked(status.equals("Sim"));

                    String prioridadeSalva = cursor.getString(cursor.getColumnIndexOrThrow("prioridade"));
                    for (int i = 0; i < spinnerPrioridade.getCount(); i++) {
                        if (spinnerPrioridade.getItemAtPosition(i).toString().equals(prioridadeSalva)) {
                            spinnerPrioridade.setSelection(i);
                            break;
                        }
                    }

                    btnSalvar.setText("Atualizar");
                }
                cursor.close();
            }
        });

        listViewTarefas.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                int idParaExcluir = listaIds.get(position);

                dbHelper.excluirTarefa(idParaExcluir);

                Toast.makeText(MainActivity.this, "Tarefa excluída com sucesso!", Toast.LENGTH_SHORT).show();

                idTarefaSelecionada = -1;
                btnSalvar.setText("Salvar");
                limparCampos();
                carregarListaTarefas();

                return true;
            }
        });
    }


    private void salvarNovaTarefa() {
        String titulo = editTitulo.getText().toString().trim();
        String disciplina = editDisciplina.getText().toString().trim();
        String dataEntrega = editDataEntrega.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        String prioridade = spinnerPrioridade.getSelectedItem().toString();

        String concluida = checkBoxConcluida.isChecked() ? "Sim" : "Não";

        if (titulo.isEmpty() || disciplina.isEmpty() || dataEntrega.isEmpty()) {
            Toast.makeText(this, "Atenção: Título, Disciplina e Data são obrigatórios!", Toast.LENGTH_LONG).show();
            return;
        }

        if (idTarefaSelecionada == -1) {
            // cadastrar
            long idRetornado = dbHelper.cadastrarTarefas(titulo, disciplina, dataEntrega, prioridade, descricao, concluida);
            if (idRetornado != -1) {
                Toast.makeText(this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao salvar no banco de dados.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // editar
            int linhasAfetadas = dbHelper.atualizarTarefa(idTarefaSelecionada, titulo, disciplina, dataEntrega, prioridade, descricao, concluida);
            if (linhasAfetadas > 0) {
                Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            }
            idTarefaSelecionada = -1;
            btnSalvar.setText("Salvar");
        }


        limparCampos();
        carregarListaTarefas();


    }

    private void limparCampos() {
        editTitulo.setText("");
        editDisciplina.setText("");
        editDataEntrega.setText("");
        editDescricao.setText("");
        checkBoxConcluida.setChecked(false);
        spinnerPrioridade.setSelection(0);
    }

    private void carregarListaTarefas() {
        listaTarefas.clear();
        listaIds.clear();

        Cursor cursor = dbHelper.listarTarefas();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String disciplina = cursor.getString(cursor.getColumnIndexOrThrow("disciplina"));
                String data = cursor.getString(cursor.getColumnIndexOrThrow("data_entrega"));
                String prioridade = cursor.getString(cursor.getColumnIndexOrThrow("prioridade"));
                String concluida = cursor.getString(cursor.getColumnIndexOrThrow("concluida"));

                String itemExibicao = "Título: " + titulo +
                        "\nDisciplina: " + disciplina +
                        "\nEntrega: " + data + " | Prioridad: " + prioridade +
                        "\nConcluida: " + concluida;

                listaIds.add(id);
                listaTarefas.add(itemExibicao);

            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
    private void carregarListaComFiltro(String filtroPrioridade) {
        listaTarefas.clear();
        listaIds.clear();

        Cursor cursor = dbHelper.listarPorPrioridade(filtroPrioridade);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String disciplina = cursor.getString(cursor.getColumnIndexOrThrow("disciplina"));
                String data = cursor.getString(cursor.getColumnIndexOrThrow("data_entrega"));
                String prioridade = cursor.getString(cursor.getColumnIndexOrThrow("prioridade"));
                String concluida = cursor.getString(cursor.getColumnIndexOrThrow("concluida"));

                String itemExibicao = "Título: " + titulo +
                        "\nDisciplina: " + disciplina +
                        "\nEntrega: " + data + " | Prioridade: " + prioridade +
                        "\nConcluida: " + concluida;

                listaIds.add(id);
                listaTarefas.add(itemExibicao);

            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
