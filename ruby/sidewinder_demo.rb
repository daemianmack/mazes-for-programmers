require_relative 'grid/grid'
require_relative 'algos/sidewinder'

seed = if ENV['SEED']
         ENV['SEED'].to_i
       else
         Random.new_seed
       end
srand(seed)

grid = Grid.new(4,4)
SideWinder.on(grid)
puts grid

puts "Seed: #{seed}"