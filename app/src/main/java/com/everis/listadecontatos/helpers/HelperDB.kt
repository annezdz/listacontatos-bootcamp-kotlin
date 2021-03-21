package com.everis.listadecontatos.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.everis.listadecontatos.feature.listacontatos.model.ContatosVO

class HelperDB(
    context: Context
) : SQLiteOpenHelper(context, NOME_BANCO, null, VERSAO_ATUAL) {

    companion object {
        private val NOME_BANCO = "contato.db"
        private val VERSAO_ATUAL = 2
    }

    val TABLE_NAME = "contato"
    val COLUMNS_ID = "id"
    val COLUMNS_NOME = "nome"
    val COLUMNS_TELEFONE = "telefone"
    val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMNS_ID INTEGER NOT NULL," +
            "$COLUMNS_NOME TEXT NOT NULL," +
            "$COLUMNS_TELEFONE TEXT NOT NULL," +
            "" +
            "PRIMARY KEY($COLUMNS_ID AUTOINCREMENT)" +
            ")"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion) {
            db?.execSQL(DROP_TABLE)
        }
        onCreate(db)
    }

    fun buscarContatos(busca: String, isBuscaPorID: Boolean = false) : List<ContatosVO> {
        val db = readableDatabase ?: return mutableListOf()
        var lista = mutableListOf<ContatosVO>()
        var where: String? = null
        var args: Array<String> = arrayOf()
        if(isBuscaPorID){
            where = "$COLUMNS_ID = ?"
            args = arrayOf("$busca")
        }else{
            where = "$COLUMNS_NOME LIKE ?"
            args = arrayOf("%$busca%")
        }
        var cursor = db.query(TABLE_NAME,null,where,args,null,null,null)
        if (cursor == null){
            db.close()
            return mutableListOf()
        }
        while(cursor.moveToNext()){
            var contato = ContatosVO(
                cursor.getInt(cursor.getColumnIndex(COLUMNS_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_NOME)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_TELEFONE))
            )
            lista.add(contato)
        }
        db.close()
        return lista
    }

    fun salvarContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        var content = ContentValues()
        content.put(COLUMNS_NOME,contato.nome)
        content.put(COLUMNS_TELEFONE,contato.telefone)
        db.insert(TABLE_NAME,null,content)
        db.close()
    }

    /*****************************************************************************************
     *                       Função Salvar por Função                                        *
     *                                                                                       *
     * fun salvarContato1(contato: ContatosVO){                                              *
     *  val db = writableDatabase ?: return                                                  *
     *  val sql = "INSERT INTO $TABLE_NAME($COLUMNS_NOME, $COLUMNS_TELEFONE) VALUES(?,?)"    *
     *  var array = arrayOf(contato.nome, contato.telefone)                                  *
     *  db.execSQL(sql,array)                                                                *
     *  db.close()                                                                           *
    }*****************************************************************************************/

    //Função Delete implementada com SQL por extenso

    fun deletarContato(id: Int) {
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $TABLE_NAME WHERE $COLUMNS_ID = ?"
        val arg = arrayOf("$id")
        db.execSQL(sql,arg)
        db.close()
    }


    /*********************************************
     *          Função Delete por função         *
     *                                           *
     * fun deletarContatoFuncao(id:Int){         *
     *  val db = writableDatabase ?: return      *
     *  var where = "id = ?"                     *
     *  val arg:Array<String> = arrayOf("$id")   *
     *  db.delete(TABLE_NAME,where,arg)          *
     *  db.close()                               *
    }*********************************************/

    fun updateContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        val sql = "UPDATE $TABLE_NAME SET $COLUMNS_NOME = ?, $COLUMNS_TELEFONE = ? WHERE $COLUMNS_ID = ?"
        val arg = arrayOf(contato.nome,contato.telefone,contato.id)
        db.execSQL(sql,arg)
        db.close()
    }
    // Função Update por função

    /***************************************************************
     *             função Update por função                        *
     *                                                             *
     * fun updateContatoporFuncao(contato: ContatosVO) {           *
     *  val db = writableDatabase ?: return                        *
     *  val content = ContentValues()                              *
     *  val where = "id =?"                                        *
     *  val arg = arrayOf("${contato.id}")                         *
     *  content.put(COLUMNS_NOME,contato.nome)                     *
     *  content.put(COLUMNS_TELEFONE,contato.telefone)             *
     *  db.update(TABLE_NAME,content,where,arg)                    *
     *  db.close()                                                 *
     *  }                                                          *
     ****************************************************************/
}