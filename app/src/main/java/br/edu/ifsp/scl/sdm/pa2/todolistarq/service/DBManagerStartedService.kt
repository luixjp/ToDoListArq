package br.edu.ifsp.scl.sdm.pa2.todolistarq.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import java.io.Serializable


class DBManagerStartedService() : Service() {

    private lateinit var database: ToDoListArqDatabase

    companion object {
        /* Para passar os resultados entre Controller e Service */
        val EXTRA_DBM = "EXTRA_DBM"
        val OK = "OK"
        val OPERATION = "OPERATION"
        val TAREFA = "TAREFA"
        val ACTION_RECEIVE_BUSCAR_TAREFAS = "ACTION_RECEIVE_BUSCAR_TAREFAS"
        val ACTION_RECEIVE_INSERIR_TAREFA = "ACTION_RECEIVE_INSERIR_TAREFA"
        val ACTION_RECEIVE_ATUALIZAR_TAREFA = "ACTION_RECEIVE_ATUALIZAR_TAREFA"
    }

    private inner class BuscarTarefasThread : Thread() {

        override fun run() {
            val result = database.getTarefaDao().recuperarTarefas()
            sendBroadcast(Intent(ACTION_RECEIVE_BUSCAR_TAREFAS).also {
                it.putParcelableArrayListExtra(EXTRA_DBM, ArrayList(result))
            })

        }
    }

    private inner class RemoverTarefaThread(private val tarefa: Tarefa) : Thread() {

        override fun run() {
            val result = database.getTarefaDao().removerTarefa(tarefa)
        }
    }

    private inner class AtualizarTarefaThread(private val tarefaEditada: Tarefa) : Thread() {

        override fun run() {
            database.getTarefaDao().atualizarTarefa(tarefaEditada)
            sendBroadcast(Intent(ACTION_RECEIVE_ATUALIZAR_TAREFA).also {
                it.putExtra(EXTRA_DBM, tarefaEditada)
            })

        }
    }

    private inner class InserirTarefaThread(private val novaTarefa: Tarefa) : Thread() {

        override fun run() {
            val result = database.getTarefaDao().inserirTarefa(novaTarefa)
            lateinit var tarefa : Tarefa
            result?.let { novoId ->
                tarefa = Tarefa(
                    novoId.toInt(),
                    novaTarefa.nome,
                    novaTarefa.realizada
                )
            }
            sendBroadcast(Intent(ACTION_RECEIVE_INSERIR_TAREFA).also {
                it.putExtra(EXTRA_DBM, tarefa)
            })

        }
    }
    private lateinit var buscarTarefasThread: BuscarTarefasThread
    private lateinit var removerTarefaThread: RemoverTarefaThread
    private lateinit var atualizarTarefaThread: AtualizarTarefaThread
    private lateinit var inserirTarefaThread: InserirTarefaThread

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        database = Room.databaseBuilder(
            this,
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()

        var operacao = intent?.getIntExtra(OPERATION,0)


        when(operacao) {
            1 -> {
                //Buscar Lista de Tarefas
                buscarTarefasThread = BuscarTarefasThread()
                buscarTarefasThread.start()
            }
            2 -> {
                //Remover Tarefa
                val tarefa = intent?.getParcelableExtra<Tarefa>(TAREFA)
                    removerTarefaThread = RemoverTarefaThread(tarefa!!)
                removerTarefaThread.start()

            }
            3 -> {
                //Atualizar Tarefa
                val tarefa = intent?.getParcelableExtra<Tarefa>(TAREFA)
                atualizarTarefaThread = AtualizarTarefaThread(tarefa!!)
                atualizarTarefaThread.start()
            }
            4 -> {

                //Inserir Tarefa
                val tarefa = intent?.getParcelableExtra<Tarefa>(TAREFA)
                inserirTarefaThread = InserirTarefaThread(tarefa!!)
                inserirTarefaThread.start()
            }
            else -> {
                Log.e("ERROR", "Valor de Operação Inválido!")}
        }
        return START_STICKY
    }
}