class SideWinder

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
  end

  def self.on(grid, debug)
    grid.each_cell do |cell|
      toss = [:n, :e].sample
      if not cell.east and not cell.north
        print_move(cell, toss, false)
      elsif not cell.north
        link_east(cell, toss)
      elsif not cell.east
        link_north(cell, toss)
      elsif toss == :e
        link_east(cell, toss)
      elsif toss == :n
        link_north(cell, toss)
      end
      if debug
        puts grid.diag_print(cell) 
        puts
      end
    end
  end
end
