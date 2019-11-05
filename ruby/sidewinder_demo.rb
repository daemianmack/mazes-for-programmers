require_relative 'grid/grid'
require_relative 'algos/sidewinder_mine'
require_relative 'seed'

seed = Seed.set(ENV['SEED'])
puts "Seed: #{seed}"

grid = Grid.new(4,4)
SidewinderMine.on(grid, ENV['DEBUG'])

puts "Complete."
puts grid
grid.to_png.save "sidewinder_mine.png"
