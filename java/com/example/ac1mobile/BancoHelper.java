package com.fadev.ac1_mobile;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tarefas.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tarefas";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_DISCIPLINA = "disciplina";
    private static final String COLUMN_DATA_ENTREGA = "data_entrega";
    private static final String COLUMN_PRIORIDADE = "prioridade";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_CONCLUIDA = "concluida";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITULO + " TEXT, " + COLUMN_CONCLUIDA + " BOOLEAN, " +
                COLUMN_DISCIPLINA + " TEXT, " + COLUMN_DATA_ENTREGA + " TEXT, " +
                COLUMN_DESCRICAO + " TEXT, " + COLUMN_PRIORIDADE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public BancoHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long cadastrarTarefas(String titulo, String disciplina, String dataEntrega,
                                 String prioridade, String descricao, String concluida ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DISCIPLINA, disciplina);
        values.put(COLUMN_DATA_ENTREGA, dataEntrega);
        values.put(COLUMN_PRIORIDADE, prioridade);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_CONCLUIDA, concluida);
        return db.insert(TABLE_NAME, null, values);
    }
    public Cursor listarTarefas(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor buscarTarefaPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int atualizarTarefa(int id, String titulo, String disciplina, String dataEntrega,
                               String prioridade, String descricao, String concluida ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_DISCIPLINA, disciplina);
        values.put(COLUMN_DATA_ENTREGA, dataEntrega);
        values.put(COLUMN_PRIORIDADE, prioridade);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_CONCLUIDA, concluida);
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public int excluirTarefa(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor listarPorPrioridade(String prioridade) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRIORIDADE + " = ?", new String[]{prioridade});
    }
}
