using DAL.Models;
using Npgsql;

namespace DAL.Data;

public class AdoDotNetTaskRepository(NpgsqlDataSource dataSource) : ITaskRepository
{
    public async Task<int> CreateAsync(TaskModel task)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();

        const string sql = "INSERT INTO tasks (name) VALUES (@Name) RETURNING id";
        await using NpgsqlCommand command = new(sql, connection);
        command.Parameters.AddWithValue("@Name", task.Name);

        int id = (int)(await command.ExecuteScalarAsync() ?? -1);
        return id;
    }

    public async Task<IEnumerable<TaskModel>> ReadAllAsync()
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();

        const string sql = "SELECT * FROM tasks";
        await using NpgsqlCommand command = new(sql, connection);

        await using NpgsqlDataReader reader = await command.ExecuteReaderAsync();
        var tasks = new List<TaskModel>();
        while (await reader.ReadAsync())
        {
            TaskModel task = new()
            {
                Id = reader.GetInt32(0),
                Name = reader.GetString(1),
            };
            tasks.Add(task);
        }

        return tasks;
    }

    public async Task<TaskModel?> ReadAsync(int id)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();

        const string sql = "SELECT * FROM tasks WHERE id = @Id";
        await using NpgsqlCommand command = new(sql, connection);
        command.Parameters.AddWithValue("@Id", id);

        await using NpgsqlDataReader reader = await command.ExecuteReaderAsync();
        if (await reader.ReadAsync())
        {
            return new TaskModel
            {
                Id = reader.GetInt32(0),
                Name = reader.GetString(1),
            };
        }

        return null;
    }

    public async Task UpdateAsync(int id, TaskModel task)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();

        const string sql = "UPDATE tasks SET name = @Name WHERE id = @Id";
        await using NpgsqlCommand command = new(sql, connection);
        command.Parameters.AddWithValue("@Name", task.Name);
        command.Parameters.AddWithValue("@Id", id);
        await command.ExecuteNonQueryAsync();
    }

    public async Task DeleteAsync(int id)
    {
        await using NpgsqlConnection connection = await dataSource.OpenConnectionAsync();

        const string sql = "DELETE FROM tasks WHERE id = @Id";
        await using NpgsqlCommand command = new(sql, connection);
        command.Parameters.AddWithValue("@Id", id);
        await command.ExecuteNonQueryAsync();
    }
}