class SidewinderMine

  def self.print_move(cell, toss, is_valid=true)
    s = "[#{cell.row}, #{cell.column}] toss: #{toss} "
    s << "(not valid)" unless is_valid
    puts s
  end
  
  def self.link_east(cell, toss)
    print_move(cell, toss)
    cell.link(cell.east)
  end

  def self.link_north(cell, toss)
    print_move(cell, toss)
    run = [cell]
    while run.last.linked?(run.last.west)
      run << run.last.west
    end
    if target = run.sample
      target.link(target.north)
    end
  end

  def self.on(grid, debug)
    grid.each_cell do |cell|
      toss = [:n, :e].sample
      if !cell.east && !cell.north
        print_move(cell, toss, false)
      elsif !cell.east || (cell.north && toss == :n)
        link_north(cell, toss)
      else
        link_east(cell, toss)
      end
      if debug
        puts grid.diag_print(cell) 
        puts
      end
    end
  end
end
