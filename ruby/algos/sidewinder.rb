class SideWinder

  def self.print_move(cell, toss, is_valid=true)
    s = "[#{cell.row}, #{cell.column}] toss: #{toss} "
    s << "(not valid)" unless is_valid
    puts s
  end

  def self.on(grid)
    grid.each_cell do |cell|
      toss = [:n, :e].sample
      if toss == :e and cell.east
        print_move(cell, toss)
        cell.link(cell.east)
      elsif toss == :n and cell.north
        print_move(cell, toss)
        this = cell
        run = [this]
        while this.linked?(this.west)
          run << this.west
          this = this.west
        end
        if run.length > 0
          target = run.sample
          target.link(target.north)
        end
      else
        print_move(cell, toss, false)
      end
      puts grid.diag_print(cell)
      puts
    end
  end
end
