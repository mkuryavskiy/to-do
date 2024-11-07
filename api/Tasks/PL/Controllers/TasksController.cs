using DAL.Data;
using DAL.Models;
using Microsoft.AspNetCore.Mvc;

namespace PL.Controllers;

[ApiController]
[Route("api/[controller]")]
public class TasksController(ITaskRepository repository) : ControllerBase
{
    [HttpGet]
    public async Task<ActionResult<IEnumerable<TaskModel>>> ReadAll()
    {
        var tasks = await repository.ReadAllAsync();
        return Ok(tasks);
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<TaskModel>> Read(int id)
    {
        TaskModel? task = await repository.ReadAsync(id);
        return task == null ? NotFound() : Ok(task);
    }

    [HttpPost]
    public async Task<ActionResult<TaskModel>> Create([FromBody] TaskModel task)
    {
        int id = await repository.CreateAsync(task);
        task.Id = id;
        return CreatedAtAction(nameof(Read), new { id }, task);
    }

    [HttpPut("{id:int}")]
    public async Task<IActionResult> Update(int id, [FromBody] TaskModel task)
    {
        TaskModel? existed = await repository.ReadAsync(id);
        if (existed == null)
        {
            return NotFound();
        }

        await repository.UpdateAsync(id, task);
        return NoContent();
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Delete(int id)
    {
        TaskModel? task = await repository.ReadAsync(id);
        if (task == null)
        {
            return NotFound();
        }

        await repository.DeleteAsync(id);
        return NoContent();
    }
}